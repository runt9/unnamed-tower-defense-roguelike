package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.SpawningCompleteEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.TargetingMode
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class EnemyService(private val grid: IndexedGridGraph, private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val enemies = mutableListOf<Enemy>()
    private var isSpawning = false

    @HandlesEvent
    fun add(event: EnemySpawnedEvent) = launchOnServiceThread {
        // TODO: Make sure this works and there's no weird race condition. Might want to use start wave event instead
        isSpawning = true
        enemies += event.enemy
    }

    @HandlesEvent
    suspend fun remove(event: EnemyRemovedEvent) = launchOnServiceThread {
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
        launchOnServiceThread {
            enemies.toList().filter { it.isAlive }.forEach { enemy ->
                enemy.statusEffects.toList().forEach { se ->
                    se.timer.tick(delta)
                    if (se.timer.isReady) {
                        enemy.statusEffects.remove(se)
                    }
                }

                val steeringOutput = SteeringAcceleration(Vector2())
                enemy.behavior.calculateSteering(steeringOutput)
                if (!steeringOutput.isZero) {
                    enemy.applySteering(delta, steeringOutput)

                    if (enemy.position.dst(grid.home.point).roundToInt() == 0) {
                        logger.info { "${enemy.id}: Enemy hit home" }
                        enemies -= enemy
                        eventBus.enqueueEventSync(EnemyRemovedEvent(enemy, false))
                    }
                }
            }
        }
    }

    fun getTowerTarget(position: Vector2, range: Float, targetingMode: TargetingMode) =
        enemies.toList()
            .sortByTargetingMode(targetingMode)
            .filter { it.isAlive }
            .find { enemy ->
                position.dst(enemy.position) <= range
            }

    override fun stopInternal() {
        enemies.clear()
    }

    fun collidesWithEnemy(position: Vector2, maxDistance: Float) = enemies.toList().firstOrNull { it.position.dst(position) <= maxDistance }

    private fun List<Enemy>.sortByTargetingMode(targetingMode: TargetingMode) = when(targetingMode) {
        TargetingMode.FRONT -> sortedBy { it.numNodesToHome() }
        TargetingMode.BACK -> sortedByDescending { it.numNodesToHome() }
        TargetingMode.STRONG -> sortedByDescending { it.maxHp }
        TargetingMode.WEAK -> sortedBy { it.maxHp }
        TargetingMode.FAST -> sortedByDescending { it.linearSpeedLimit }
        TargetingMode.SLOW -> sortedBy { it.linearSpeedLimit }
        TargetingMode.NEAR_DEATH -> sortedBy { it.currentHp }
        TargetingMode.HEALTHIEST -> sortedByDescending { it.currentHp }
    }

    fun enemiesInRange(fromPosition: Vector2, range: Float) = enemies.filter { it.position.dst(fromPosition) <= range }
}
