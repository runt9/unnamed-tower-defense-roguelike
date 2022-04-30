package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.loot.definition.LegendaryPassiveEffectDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.action.TowerActionDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.towerAction.TowerAction
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.dynamicInjectCheckAssignableFrom
import com.runt9.untdrl.util.ext.dynamicInjectCheckInterfaceContains
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.view.duringRun.MAX_TOWER_LEVEL
import com.runt9.untdrl.view.duringRun.TOWER_SPECIALIZATION_LEVEL
import ktx.assets.async.AssetStorage
import kotlin.math.roundToInt

class TowerService(
    eventBus: EventBus,
    registry: RunServiceRegistry,
    private val assets: AssetStorage
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val towers = mutableListOf<Tower>()
    private val towerChangeCbs = mutableMapOf<Int, MutableList<suspend (Tower) -> Unit>>()
    private val globalXpModifiers = mutableListOf<Float>()

    fun add(tower: Tower) = launchOnServiceThread {
        towers += tower
    }

    @HandlesEvent
    fun add(event: TowerPlacedEvent) = add(event.tower)

    // TODO: Selling towers
    fun remove(tower: Tower) {
        towers -= tower
    }

    fun addGlobalXpModifier(amt: Float) = launchOnServiceThread {
        globalXpModifiers += amt
    }

    fun onTowerChange(id: Int, cb: suspend (Tower) -> Unit) {
        towerChangeCbs.computeIfAbsent(id) { mutableListOf() } += cb
    }

    fun removeTowerChangeCb(id: Int, cb: suspend (Tower) -> Unit) {
        towerChangeCbs[id]?.remove(cb)
    }

    fun forEachTower(fn: (Tower) -> Unit) = towers.toList().forEach(fn)

    override fun tick(delta: Float) {
        launchOnServiceThread {
            towers.toList().forEach { tower ->
                tower.action.act(delta)
            }
        }
    }

    fun injectTowerAction(tower: Tower): TowerAction {
        val action = dynamicInject(
            tower.definition.action.actionClass,
            dynamicInjectCheckInterfaceContains(TowerActionDefinition::class.java) to tower.definition.action,
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

            levelUp(tower)
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

            definition.attrs.forEach { (type, def) ->
                // TODO: This is wrong, tower growth needs to modify the base value directly, not stack with other modifiers
                attrMods += AttributeModifier(
                    type,
                    flatModifier = if (def.growthType == FLAT) def.growthPerLevel else 0f,
                    percentModifier = if (def.growthType == PERCENT) def.growthPerLevel else 0f
                )
            }

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
        tower.apply {
            attrs.forEach { (type, attr) ->
                val baseValue = attrBase[type] ?: 0f
                var totalFlat = 0f
                var totalPercent = 0f

                attrMods.filter { it.type == type }.forEach {
                    totalFlat += it.flatModifier
                    totalPercent += it.percentModifier
                }

                val newValue = ((baseValue + totalFlat) * (1 + (totalPercent / 100)))
                if (newValue != attr()) {
                    attr(newValue)
                }
            }
        }

        tower.changed()
    }

    suspend fun addCore(id: Int, core: TowerCore) = launchOnServiceThread {
        withTower(id) {
            cores += core
            attrMods += core.modifiers
            if (core.passive != null) {
                val passiveEffect = dynamicInject(
                    core.passive.effect.effectClass,
                    dynamicInjectCheckInterfaceContains(LegendaryPassiveEffectDefinition::class.java) to core.passive.effect,
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
                dynamicInjectCheckInterfaceContains(TowerSpecializationEffectDefinition::class.java) to specialization.effect,
                dynamicInjectCheckAssignableFrom(Tower::class.java) to this
            )
            specializationEffect.init()
            specializationEffect.apply()

            appliedSpecialization = specialization

            recalculateAttrs(this)
        }
    }

    suspend fun newTower(towerDef: TowerDefinition): Tower {
        val tower = Tower(towerDef, assets[towerDef.texture.assetFile])
        recalculateAttrs(tower)
        tower.action = injectTowerAction(tower)
        return tower
    }

    private suspend fun Tower.changed() = towerChangeCbs[id]?.toList()?.forEach { it(this) }
    private suspend fun withTower(id: Int, fn: suspend Tower.() -> Unit) = towers.find { it.id == id }?.fn()
    suspend fun update(id: Int, fn: suspend Tower.() -> Unit) = withTower(id) {
        fn()
        changed()
    }
}
