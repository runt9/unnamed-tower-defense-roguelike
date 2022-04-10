package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class TowerService(private val enemyService: EnemyService, private val projectileService: ProjectileService, eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val towers = mutableListOf<Tower>()

    @HandlesEvent
    fun add(event: TowerPlacedEvent) = runOnServiceThread {
        towers += event.tower
    }

    fun remove(tower: Tower) {
        towers -= tower
    }

    override fun tick(delta: Float) {
        towers.forEach { tower ->
            val steeringOutput = SteeringAcceleration(Vector2())

            tower.attackTimer.tick(delta)

            val target = enemyService.getTowerTarget(tower) ?: return@forEach

            tower.setTarget(target)

            tower.behavior.calculateSteering(steeringOutput)
            if (!steeringOutput.isZero) {
                tower.applySteering(delta, steeringOutput)
            }

            if (tower.attackTimer.isReady && steeringOutput.isZero) {
                projectileService.add(tower.spawnProjectile())
                tower.attackTimer.reset(false)
            }
        }
    }

    fun isNoTowerPositionOverlap(tower: Tower) = towers.none { it.position == tower.position }

    override fun stopInternal() {
        towers.clear()
    }
}
