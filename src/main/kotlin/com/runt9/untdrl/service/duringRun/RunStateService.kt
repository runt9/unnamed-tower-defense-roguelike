package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus

class RunStateService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private var runState: RunState? = null

    fun load(): RunState {
        if (runState == null) {
            runState = RunState()
        }

        return runState!!.copy()
    }

    fun save(runState: RunState) {
        if (runState != this.runState) {
            logger.info { "Saving run state" }
            this.runState = runState
            eventBus.enqueueEventSync(RunStateUpdated(runState.copy()))
            // TODO: This should also flush the current state to disk
        }
    }

    override fun stopInternal() {
        runState = null
    }
}
