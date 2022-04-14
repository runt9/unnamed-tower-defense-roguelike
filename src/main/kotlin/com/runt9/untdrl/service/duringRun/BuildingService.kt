package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class BuildingService(private val enemyService: EnemyService, private val projectileService: ProjectileService, eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val buildings = mutableListOf<Building>()

    @HandlesEvent
    fun add(event: BuildingPlacedEvent) = runOnServiceThread {
        buildings += event.building
    }

    fun remove(building: Building) {
        buildings -= building
    }

    override fun tick(delta: Float) {
        buildings.toList().forEach { building ->
            val steeringOutput = SteeringAcceleration(Vector2())

            building.attackTimer.tick(delta)

            val target = enemyService.getBuildingTarget(building) ?: return@forEach

            building.setTarget(target)

            building.behavior.calculateSteering(steeringOutput)
            if (!steeringOutput.isZero) {
                building.applySteering(delta, steeringOutput)
            }

            if (building.attackTimer.isReady && steeringOutput.isZero) {
                projectileService.add(building.spawnProjectile())
                building.attackTimer.reset(false)
            }
        }
    }

    fun isNoBuildingPositionOverlap(building: Building) = buildings.none { it.position == building.position }

    override fun stopInternal() {
        buildings.clear()
    }

    fun getBuildingAtPoint(clickPoint: Vector2) = buildings.find { it.position == clickPoint }
}
