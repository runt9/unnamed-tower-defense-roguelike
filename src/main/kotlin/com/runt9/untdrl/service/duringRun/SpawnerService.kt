package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.enemy.Spawner
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.SpawnerPlacedEvent
import com.runt9.untdrl.model.event.SpawningCompleteEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import ktx.assets.async.AssetStorage

class SpawnerService(
    private val assets: AssetStorage,
    private val grid: IndexedGridGraph,
    private val eventBus: EventBus,
    private val runStateService: RunStateService,
    registry: RunServiceRegistry
) : RunService(eventBus, registry) {
    private var isSpawning = false
    private val spawners = mutableListOf<Spawner>()

    @HandlesEvent
    fun addSpawner(event: SpawnerPlacedEvent) = runOnServiceThread {
        val spawner = Spawner(event.node, assets[UnitTexture.ENEMY.assetFile])
        grid.calculateSpawnerPath(spawner)
        spawners += spawner
        recalculateSpawner(spawner)
    }

    @HandlesEvent(WaveStartedEvent::class)
    fun startSpawning() = runOnServiceThread {
        isSpawning = true
    }

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun prepNextWave() = spawners.forEach(::recalculateSpawner)

    private fun recalculateSpawner(spawner: Spawner) {
        val waveNum = runStateService.load().wave
        spawner.enemiesToSpawn = waveNum * 2
        // TODO: Recalculate time between spawns
    }

    override fun tick(delta: Float) {
        runOnServiceThread {
            if (!isSpawning) return@runOnServiceThread

            val runState = runStateService.load()
            var checkedSpawn = false
            spawners.forEach { spawner ->
                if (spawner.enemiesToSpawn == 0) return@forEach

                checkedSpawn = true

                spawner.enemyDelayTimer.also { timer ->
                    timer.tick(delta)
                    if (timer.isReady) {
                        val enemy = spawner.spawnEnemy(runState.wave)
                        eventBus.enqueueEvent(EnemySpawnedEvent(enemy))
                        spawner.enemiesToSpawn--
                        timer.reset()
                    }
                }
            }

            if (!checkedSpawn) {
                isSpawning = false
                eventBus.enqueueEvent(SpawningCompleteEvent())
            }
        }
    }
}
