package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.enemy.Spawner
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.SpawnerPlacedEvent
import com.runt9.untdrl.model.event.SpawningCompleteEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import ktx.assets.async.AssetStorage

class SpawnerService(
    private val assets: AssetStorage,
    private val grid: IndexedGridGraph,
    private val eventBus: EventBus,
    private val runStateService: RunStateService,
    private val randomizer: RandomizerService,
    registry: RunServiceRegistry
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()

    private var isSpawning = false
    private val spawners = mutableListOf<Spawner>()

    @HandlesEvent
    fun addSpawner(event: SpawnerPlacedEvent) = launchOnServiceThread {
        val chunk = event.chunk
        val spawner = Spawner(event.node, assets[UnitTexture.ENEMY.assetFile], chunk.biome)
        grid.calculateSpawnerPath(spawner)
        spawners += spawner
        recalculateSpawner(spawner)
        chunk.spawner = spawner
    }

    @HandlesEvent(WaveStartedEvent::class)
    fun startSpawning() = launchOnServiceThread {
        isSpawning = true
    }

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun prepNextWave() = launchOnServiceThread {  spawners.forEach(::recalculateSpawner) }

    private fun recalculateSpawner(spawner: Spawner) {
        val waveNum = runStateService.load().wave
        spawner.enemiesToSpawn = waveNum * 2
        spawner.enemyDelayTimer.reset(false)
        spawner.currentEnemySpawnType = spawner.enemyTypesToSpawn.random(randomizer.rng)
        // TODO: Recalculate time between spawns
    }

    override fun tick(delta: Float) {
        launchOnServiceThread {
            if (!isSpawning) return@launchOnServiceThread

            val runState = runStateService.load()
            var checkedSpawn = false
            spawners.forEach { spawner ->
                // TODO: Somehow a spawner can double spawn
                if (spawner.enemiesToSpawn <= 0) return@forEach

                checkedSpawn = true

                spawner.enemyDelayTimer.also { timer ->
                    timer.tick(delta)
                    if (timer.isReady) {
                        logger.info { "${spawner.id} Spawning enemy" }
                        timer.reset()
                        spawner.enemiesToSpawn--
                        val enemy = spawner.spawnEnemy(runState.wave)
                        eventBus.enqueueEvent(EnemySpawnedEvent(enemy))
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
