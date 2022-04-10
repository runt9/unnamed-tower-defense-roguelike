package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.SpawningCompleteEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class EnemyService(private val grid: IndexedGridGraph, private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
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
        enemies.toList().forEach { enemy ->
            val steeringOutput = SteeringAcceleration(Vector2())
            enemy.behavior.calculateSteering(steeringOutput)
            if (!steeringOutput.isZero) {
                enemy.applySteering(delta, steeringOutput)

                if (enemy.position.dst(grid.home.point).roundToInt() == 0) {
                    eventBus.enqueueEventSync(EnemyRemovedEvent(enemy))
                }
            }
        }
    }

    fun getTowerTarget(tower: Tower) =
        enemies.sortedBy { it.position.dst(7f, 4f) }
            .find { enemy ->
                tower.position.dst(enemy.position) <= 2
            }

    override fun stopInternal() {
        enemies.clear()
    }
}
