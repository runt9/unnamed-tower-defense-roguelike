package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.RunEndEvent
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class RunStateService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private var runState: RunState? = null

    fun load(): RunState {
        if (runState == null) {
            runState = RunState()
        }

        return runState!!.copy()
    }

    fun save(runState: RunState) = runOnServiceThread {
        if (runState != this@RunStateService.runState) {
            logger.info { "Saving run state" }
            this@RunStateService.runState = runState
            eventBus.enqueueEventSync(RunStateUpdated(runState.copy()))
            // TODO: This should also flush the current state to disk
        }
    }

    override fun stopInternal() {
        runState = null
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() = runOnServiceThread {
        update { wave++ }

        // TODO: Maybe not the right way to handle this, will figure out later
        eventBus.enqueueEvent(PrepareNextWaveEvent())
    }

    @HandlesEvent
    fun enemyRemoved(event: EnemyRemovedEvent) = runOnServiceThread {
        if (event.wasKilled) return@runOnServiceThread

        // TODO: This is where enemy damage goes if enemies can have different damage values. Boss probably does, for example
        update {
            if (--hp <= 0) {
                eventBus.enqueueEventSync(RunEndEvent(false))
            }
        }
    }

    @HandlesEvent
    fun buildingPlaced(event: BuildingPlacedEvent) {
        val goldCost = event.building.definition.goldCost
        update {
            gold -= goldCost
        }
    }

    fun update(update: RunState.() -> Unit) {
        load().apply {
            update()
            save(this)
        }
    }
}
