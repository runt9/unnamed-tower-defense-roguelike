package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Mine
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class MineService(
    eventBus: EventBus,
    registry: RunServiceRegistry,
    private val enemyService: EnemyService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val mines = mutableListOf<Mine>()

    fun add(mine: Mine) = launchOnServiceThread {
        mines += mine
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() = launchOnServiceThread {
        mines.forEach { it.die() }
        mines.clear()
    }

    override fun tick(delta: Float) {
        launchOnServiceThread {
            mines.toList().forEach { mine ->
                if (!mine.armed) {
                    val steeringOutput = SteeringAcceleration(Vector2())
                    mine.behavior.calculateSteering(steeringOutput)
                    if (!steeringOutput.isZero) {
                        mine.applySteering(delta, steeringOutput)
                        if (mine.position.dst(mine.landingSpot.position) <= 0.1f) {
                            mine.behavior.isEnabled = false
                            mine.armed = true
                        }
                    }
                }

                if (mine.armed) {
                    val collidedEnemy = enemyService.collidesWithEnemy(mine.position, 0.5f)

                    if (collidedEnemy != null) {
                        enemyService.attackEnemy(DamageSource.MINE, mine.owner, collidedEnemy, mine.position)
                        despawnMine(mine)
                    }
                }
            }
        }
    }

    private suspend fun despawnMine(mine: Mine) {
        if (mines.remove(mine)) {
            mine.die()
        }
    }

    override fun stopInternal() {
        mines.clear()
    }
}
