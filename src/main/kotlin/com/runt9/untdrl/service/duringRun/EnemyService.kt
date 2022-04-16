package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.SpawningCompleteEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class EnemyService(private val grid: IndexedGridGraph, private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val enemies = mutableListOf<Enemy>()
    private var isSpawning = false

    @HandlesEvent
    fun add(event: EnemySpawnedEvent) = runOnServiceThread {
        // TODO: Make sure this works and there's no weird race condition. Might want to use start wave event instead
        isSpawning = true
        enemies += event.enemy
    }

    @HandlesEvent
    suspend fun remove(event: EnemyRemovedEvent) = runOnServiceThread {
        enemies -= event.enemy

        if (!isSpawning && enemies.isEmpty()) {
            eventBus.enqueueEvent(WaveCompleteEvent())
        }
    }

    @HandlesEvent(SpawningCompleteEvent::class)
    fun spawningComplete() {
        isSpawning = false
    }

    override fun tick(delta: Float) {
        runOnServiceThread {
            enemies.toList().filter { it.isAlive }.forEach { enemy ->
                val steeringOutput = SteeringAcceleration(Vector2())
                enemy.behavior.calculateSteering(steeringOutput)
                if (!steeringOutput.isZero) {
                    enemy.applySteering(delta, steeringOutput)

                    if (enemy.position.dst(grid.home.point).roundToInt() == 0) {
                        logger.info { "${enemy.id}: Enemy hit home" }
                        enemies -= enemy
                        enemy.die()
                        eventBus.enqueueEventSync(EnemyRemovedEvent(enemy, false))
                    }
                }
            }
        }
    }

    fun getBuildingTarget(position: Vector2, range: Int) =
        enemies.toList()
            .sortedBy { it.numNodesToHome() }
            .filter { it.isAlive }
            .find { enemy ->
                position.dst(enemy.position) <= range
            }

    override fun stopInternal() {
        enemies.clear()
    }
}
