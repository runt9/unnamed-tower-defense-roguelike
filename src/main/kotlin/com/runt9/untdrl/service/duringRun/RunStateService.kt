package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.RunEndEvent
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

typealias RunStatePreSaveCallback = (old: RunState, new: RunState) -> RunState

class RunStateService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private lateinit var runState: RunState
    private val preSaveCallbacks = mutableListOf<RunStatePreSaveCallback>()

    // TODO: This should probably jump into the service thread to load
    fun load() = runState.copy()

    fun save(runState: RunState) {
        if (!this@RunStateService::runState.isInitialized || runState != this@RunStateService.runState) {
            logger.debug { "Saving run state" }
            var finalRunState = runState.copy()

            preSaveCallbacks.forEach { cb ->
                finalRunState = cb(load(), finalRunState.copy())
            }

            this@RunStateService.runState = finalRunState
            eventBus.enqueueEventSync(RunStateUpdated(finalRunState.copy()))
            // TODO: This should also flush the current state to disk
        }
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() = launchOnServiceThread {
        update { wave++ }

        // TODO: Maybe not the right way to handle this, will figure out later
        eventBus.enqueueEvent(PrepareNextWaveEvent())
    }

    @HandlesEvent
    fun enemyRemoved(event: EnemyRemovedEvent) = launchOnServiceThread {
        if (event.wasKilled) return@launchOnServiceThread

        // TODO: This is where enemy damage goes if enemies can have different damage values. Boss probably does, for example
        update {
            if (--hp <= 0) {
                eventBus.enqueueEventSync(RunEndEvent(false))
            }
        }
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        val goldCost = event.tower.definition.goldCost
        update {
            gold -= goldCost
        }
    }

    fun update(update: RunState.() -> Unit) = launchOnServiceThread {
        load().apply {
            update()
            save(this)
        }
    }

    fun beforeSave(preSaveCallback: RunStatePreSaveCallback) {
        preSaveCallbacks += preSaveCallback
    }

    fun removeBeforeSave(preSaveCallback: RunStatePreSaveCallback) {
        preSaveCallbacks -= preSaveCallback
    }
}
