package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.loot.definition.LegendaryPassiveEffectDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.action.TowerActionDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.towerAction.TowerAction
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.dynamicInjectCheckAssignableFrom
import com.runt9.untdrl.util.ext.dynamicInjectCheckIsSubclassOf
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.view.duringRun.MAX_TOWER_LEVEL
import com.runt9.untdrl.view.duringRun.TOWER_SPECIALIZATION_LEVEL
import kotlin.math.roundToInt

class TowerService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val towers = mutableListOf<Tower>()
    val allTowers get() = towers.toList()
    private val towerChangeCbs = mutableMapOf<Int, MutableList<suspend (Tower) -> Unit>>()
    private val globalXpModifiers = mutableListOf<Float>()
    private val globalAttrGrowthModifiers = mutableListOf<Float>()

    fun add(tower: Tower) = launchOnServiceThread {
        towers += tower
    }

    @HandlesEvent
    fun add(event: TowerPlacedEvent) {
        event.tower.action = injectTowerAction(event.tower)
        add(event.tower)
    }

    // TODO: Selling towers
    fun remove(tower: Tower) {
        towers -= tower
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        forEachTower { tower ->
            tower.attrMods.removeIf { it.isTemporary }
            recalculateAttrsSync(tower)
        }
    }

    fun addGlobalXpModifier(amt: Float) = launchOnServiceThread {
        globalXpModifiers += amt
    }

    fun addGlobalAttrGrowthModifier(amt: Float) = launchOnServiceThread {
        globalAttrGrowthModifiers += amt
    }

    fun onTowerChange(id: Int, cb: suspend (Tower) -> Unit) {
        towerChangeCbs.computeIfAbsent(id) { mutableListOf() } += cb
    }

    fun removeTowerChangeCb(id: Int, cb: suspend (Tower) -> Unit) {
        towerChangeCbs[id]?.remove(cb)
    }

    fun forEachTower(fn: (Tower) -> Unit) {
        allTowers.forEach {
            fn(it)
        }
    }

    override fun tick(delta: Float) {
        launchOnServiceThread {
            allTowers.forEach { tower ->
                tower.action.act(delta)
            }
        }
    }

    private fun injectTowerAction(tower: Tower): TowerAction {
        val action = dynamicInject(
            tower.definition.action.actionClass,
            dynamicInjectCheckIsSubclassOf(TowerActionDefinition::class.java) to tower.definition.action,
            dynamicInjectCheckAssignableFrom(Tower::class.java) to tower
        )
        action.init()
        return action
    }

    fun isNoTowerPositionOverlap(tower: Tower) = towers.none { it.position == tower.position }

    override fun stopInternal() {
        towers.clear()
    }

    fun getTowerAtPoint(clickPoint: Vector2) = towers.find { it.position == clickPoint }

    // TODO: This may not work in the long run. Faster attacking towers get to hit more enemies meaning they get more XP than slower attacking towers
    //  that might do more damage. But some sort of "percent damage share" may not work since something like a "slow tower" does no damage.
    fun gainXpSync(tower: Tower, xp: Int) = launchOnServiceThread { gainXp(tower, xp) }
    suspend fun gainXp(tower: Tower, xp: Int) {
        tower.apply {
            if (level == MAX_TOWER_LEVEL) {
                return
            }

            val totalXpModifier = 1 + globalXpModifiers.sum() + localXpModifiers.sum()

            this.xp += (xp * totalXpModifier).roundToInt()
            if (this.xp < xpToLevel) {
                tower.changed()
                return
            }

            while (tower.xp >= tower.xpToLevel) {
                levelUp(tower)
            }
        }
    }

    private suspend fun levelUp(tower: Tower) {
        tower.apply {
            level++
            if (level == MAX_TOWER_LEVEL) {
                tower.changed()
                return
            }

            xp -= xpToLevel
            xpToLevel = (xpToLevel * 1.5f).roundToInt()

            if (level >= TOWER_SPECIALIZATION_LEVEL) {
                tower.canSpecialize = true
            }

            recalculateAttrs(tower)
        }
    }

    fun recalculateAttrsSync(tower: Tower) = launchOnServiceThread {
        recalculateAttrs(tower)
    }

    suspend fun recalculateAttrs(tower: Tower) {
        var anyChanged = false
        tower.apply {
            val mods = attrMods.toList()
            attrs.forEach { (type, attr) ->
                val baseValue = attrBase[type] ?: 0f

                val growthPair = attrGrowth[type] ?: Pair(FLAT, 0f)
                val growthAmount = growthPair.second * (tower.level - 1) * (1 + globalAttrGrowthModifiers.sum())
                val levelGrownBaseValue = if (growthPair.first == FLAT) baseValue + growthAmount else baseValue * (1 + (growthAmount / 100))

                var totalFlat = 0f
                var totalPercent = 0f

                mods.filter { it.type == type }.forEach {
                    totalFlat += it.flatModifier
                    totalPercent += it.percentModifier
                }

                val newValue = ((levelGrownBaseValue + totalFlat) * (1 + (totalPercent / 100)))
                if (newValue != attr()) {
                    attr(newValue)
                    anyChanged = true
                }
            }
        }

        if (anyChanged) {
            tower.changed()
        }
    }

    suspend fun addCore(id: Int, core: TowerCore) = launchOnServiceThread {
        withTower(id) {
            cores += core
            addAttributeModifiers(core.modifiers)
            if (core.passive != null) {
                val passiveEffect = dynamicInject(
                    core.passive.effect.effectClass,
                    dynamicInjectCheckIsSubclassOf(LegendaryPassiveEffectDefinition::class.java) to core.passive.effect,
                    dynamicInjectCheckAssignableFrom(Tower::class.java) to this
                )
                passiveEffect.init()
                passiveEffect.apply()
            }
            recalculateAttrs(this)
        }
    }

    suspend fun applySpecializationToTower(id: Int, specialization: TowerSpecializationDefinition) {
        logger.info { "Applying ${specialization.name} to $id" }

        withTower(id) {
            val specializationEffect = dynamicInject(
                specialization.effect.effectClass,
                dynamicInjectCheckIsSubclassOf(TowerSpecializationEffectDefinition::class.java) to specialization.effect,
                dynamicInjectCheckAssignableFrom(Tower::class.java) to this
            )
            specializationEffect.init()
            specializationEffect.apply()

            appliedSpecialization = specialization
            appliedSpecializationEffect = specializationEffect

            recalculateAttrs(this)
            eventBus.enqueueEvent(TowerSpecializationSelected(this, specialization))
        }
    }

    suspend fun newTower(towerDef: TowerDefinition): Tower {
        val tower = Tower(towerDef)
        recalculateAttrs(tower)
        return tower
    }

    private suspend fun Tower.changed() = towerChangeCbs[id]?.toList()?.forEach { it(this) }
    private suspend fun withTower(id: Int, fn: suspend Tower.() -> Unit) = towers.find { it.id == id }?.fn()
    suspend fun update(id: Int, fn: suspend Tower.() -> Unit) = withTower(id) {
        fn()
        changed()
    }

    fun towersInRange(tower: Tower) = towersInRange(tower.position, tower.range).filter { it != tower }
    fun towersInRange(position: Vector2, range: Float) = towers.filter { it.position.dst(position) <= range }

    fun removeAttributes(tower: Tower, vararg attrsToRemove: AttributeType) = launchOnServiceThread {
        tower.apply {
            attrsToRemove.forEach { attr ->
                attrs.remove(attr)
                attrBase.remove(attr)
                attrGrowth.remove(attr)
                attrMods.removeIf { it.type == attr }
            }
            changed()
        }
    }
}
