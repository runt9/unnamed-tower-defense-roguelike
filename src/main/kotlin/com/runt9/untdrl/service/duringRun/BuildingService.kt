package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.action.BuildingActionDefinition
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.building.upgrade.BuildingUpgrade
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.loot.definition.LegendaryPassiveEffectDefinition
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.buildingAction.BuildingAction
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.dynamicInjectCheckAssignableFrom
import com.runt9.untdrl.util.ext.dynamicInjectCheckInterfaceContains
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.view.duringRun.MAX_BUILDING_LEVEL
import ktx.assets.async.AssetStorage
import kotlin.math.roundToInt

class BuildingService(
    private val eventBus: EventBus,
    registry: RunServiceRegistry,
    private val assets: AssetStorage,
    private val randomizer: RandomizerService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val buildings = mutableListOf<Building>()
    private val buildingChangeCbs = mutableMapOf<Int, MutableList<suspend (Building) -> Unit>>()
    private val globalXpModifiers = mutableListOf<Float>()

    fun add(building: Building) = launchOnServiceThread {
        buildings += building
    }

    @HandlesEvent
    fun add(event: BuildingPlacedEvent) = add(event.building)

    // TODO: Selling buildings
    fun remove(building: Building) {
        buildings -= building
    }

    fun addGlobalXpModifier(amt: Float) = launchOnServiceThread {
        globalXpModifiers += amt
    }

    fun onBuildingChange(id: Int, cb: suspend (Building) -> Unit) {
        buildingChangeCbs.computeIfAbsent(id) { mutableListOf() } += cb
    }

    fun removeBuildingChangeCb(id: Int, cb: suspend (Building) -> Unit) {
        buildingChangeCbs[id]?.remove(cb)
    }

    override fun tick(delta: Float) {
        launchOnServiceThread {
            buildings.toList().forEach { building ->
                building.action.act(delta)
            }
        }
    }

    fun injectBuildingAction(building: Building): BuildingAction {
        val action = dynamicInject(
            building.definition.action.actionClass,
            dynamicInjectCheckInterfaceContains(BuildingActionDefinition::class.java) to building.definition.action,
            dynamicInjectCheckAssignableFrom(Building::class.java) to building
        )
        action.init()
        return action
    }

    fun isNoBuildingPositionOverlap(building: Building) = buildings.none { it.position == building.position }

    override fun stopInternal() {
        buildings.clear()
    }

    fun getBuildingAtPoint(clickPoint: Vector2) = buildings.find { it.position == clickPoint }

    suspend fun gainXp(building: Building, xp: Int) {
        building.apply {
            if (level == MAX_BUILDING_LEVEL) {
                return
            }

            val totalXpModifier = 1 + globalXpModifiers.sum() + localXpModifiers.sum()

            this.xp += (xp * totalXpModifier).roundToInt()
            if (this.xp < xpToLevel) {
                building.changed()
                return
            }

            levelUp(building)
        }
    }

    private suspend fun levelUp(building: Building) {
        building.apply {
            level++
            if (level == MAX_BUILDING_LEVEL) {
                building.changed()
                return
            }

            xp -= xpToLevel
            xpToLevel = (xpToLevel * 1.5f).roundToInt()
            upgradePoints++

            definition.attrs.forEach { (type, def) ->
                attrMods += AttributeModifier(
                    type,
                    flatModifier = if (def.growthType == FLAT) def.growthPerLevel else 0f,
                    percentModifier = if (def.growthType == PERCENT) def.growthPerLevel else 0f
                )
            }

            recalculateAttrs(building)
        }
    }

    suspend fun recalculateAttrs(building: Building) {
        building.apply {
            attrs.forEach { (type, attr) ->
                val baseValue = definition.attrs[type]!!.baseValue
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

        building.changed()
    }

    suspend fun addCore(id: Int, core: TowerCore) = launchOnServiceThread {
        withBuilding(id) {
            cores += core
            attrMods += core.modifiers
            if (core.passive != null) {
                val passiveEffect = dynamicInject(
                    core.passive.effect.effectClass,
                    dynamicInjectCheckInterfaceContains(LegendaryPassiveEffectDefinition::class.java) to core.passive.effect,
                    dynamicInjectCheckAssignableFrom(Building::class.java) to this
                )
                passiveEffect.init()
                passiveEffect.apply()
            }
            recalculateAttrs(this)
        }
    }

    suspend fun applyUpgradeToBuilding(id: Int, upgrade: BuildingUpgrade) {
        logger.info { "Applying ${upgrade.name} to $id" }
        withBuilding(id) {
            upgradePoints--
            availableUpgrades -= upgrade
            appliedUpgrades += upgrade
            selectableUpgrades -= upgrade

            availableUpgrades.removeIf { it.isExclusiveOf(upgrade) }
            selectableUpgrades.removeIf { it.isExclusiveOf(upgrade) }

            addUpgrades()
            changed()
        }
    }

    private fun Building.addUpgrades() {
        val newUpgrades = definition.upgrades.filter { up ->
            // Exclude upgrades already made available, already applied, or anything made exclusive
            if (availableUpgrades.contains(up)) return@filter false
            if (appliedUpgrades.contains(up)) return@filter false
            if (appliedUpgrades.any { it.isExclusiveOf(up) }) return@filter false

            // Only include upgrades with no dependencies or satisfied dependencies
            return@filter appliedUpgrades.containsAll(up.dependsOn)
        }

        availableUpgrades += newUpgrades
        while (selectableUpgrades.size < selectableUpgradeOptions && selectableUpgrades.size < availableUpgrades.size) {
            selectableUpgrades += availableUpgrades.filter { !selectableUpgrades.contains(it) }.random(randomizer.rng)
        }
    }

    suspend fun newBuilding(buildingDef: BuildingDefinition): Building {
        val building = Building(buildingDef, assets[buildingDef.texture.assetFile])
        building.addUpgrades()
        recalculateAttrs(building)
        building.action = injectBuildingAction(building)
        return building
    }

    private suspend fun Building.changed() = buildingChangeCbs[id]?.forEach { it(this) }
    private suspend fun withBuilding(id: Int, fn: suspend Building.() -> Unit) = buildings.find { it.id == id }?.fn()
    suspend fun update(id: Int, fn: suspend Building.() -> Unit) = withBuilding(id) {
        fn()
        changed()
    }
}
