package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.action.BuildingActionDefinition
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.service.buildingAction.BuildingAction
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import ktx.reflect.reflect

class BuildingService(eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val buildings = mutableListOf<Building>()

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

    @HandlesEvent
    fun add(event: BuildingPlacedEvent) = runOnServiceThread {
        buildings += event.building
    }

    fun remove(building: Building) {
        buildings -= building
    }

    override fun tick(delta: Float) {
        runOnServiceThread {
            buildings.toList().forEach { building ->
                building.action.act(delta)
            }
        }
    }

    fun isNoBuildingPositionOverlap(building: Building) = buildings.none { it.position == building.position }

    override fun stopInternal() {
        buildings.clear()
    }

    fun getBuildingAtPoint(clickPoint: Vector2) = buildings.find { it.position == clickPoint }
}
