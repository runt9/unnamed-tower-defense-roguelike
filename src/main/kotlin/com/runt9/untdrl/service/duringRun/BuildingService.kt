package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.action.BuildingActionDefinition
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.service.buildingAction.BuildingAction
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.view.duringRun.MAX_BUILDING_LEVEL
import ktx.reflect.reflect
import kotlin.math.roundToInt

class BuildingService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val buildings = mutableListOf<Building>()
    private val buildingChangeCbs = mutableMapOf<Int, MutableList<suspend (Building) -> Unit>>()

    @HandlesEvent
    fun add(event: BuildingPlacedEvent) = runOnServiceThread {
        buildings += event.building
    }

    fun remove(building: Building) {
        buildings -= building
    }

    fun onBuildingChange(id: Int, cb: suspend (Building) -> Unit) {
        buildingChangeCbs.computeIfAbsent(id) { mutableListOf() } += cb
    }

    fun removeBuildingChangeCb(id: Int, cb: suspend (Building) -> Unit) {
        buildingChangeCbs[id]?.remove(cb)
    }

    override fun tick(delta: Float) {
        runOnServiceThread {
            buildings.toList().forEach { building ->
                building.action.act(delta)
            }
        }
    }

    fun injectBuildingAction(building: Building): BuildingAction {
        val definition = building.definition.action
        val constructor = reflect(definition.actionClass).constructor
        val parameters = constructor.parameterTypes.map {
            return@map when {
                it.interfaces.contains(BuildingActionDefinition::class.java) -> definition
                it.isAssignableFrom(Building::class.java) -> building
                else -> Injector.getProvider(it).invoke()
            }
        }.toTypedArray()
        val action = constructor.newInstance(*parameters) as BuildingAction
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

            this.xp += xp
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

    private suspend fun Building.changed() = buildingChangeCbs[id]?.forEach { it(this) }
}
