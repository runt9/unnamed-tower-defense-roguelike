package com.runt9.untdrl.view.duringRun.ui

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller

class DuringRunUiController(private val eventBus: EventBus, private val runStateService: RunStateService) : Controller {
    override val vm = DuringRunUiViewModel()
    override val view = DuringRunUiView(this, vm)
    private val children = mutableListOf<Controller>()

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        children.forEach(Disposable::dispose)
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun addChunk() {
        vm.actionsVisible(false)
        eventBus.enqueueEventSync(NewChunkEvent())
    }

    @HandlesEvent(ChunkPlacedEvent::class)
    fun chunkPlaced() {
        vm.actionsVisible(true)
        vm.chunkPlacementRequired(false)
    }

    @HandlesEvent(ChunkCancelledEvent::class)
    fun chunkCancelled() {
        vm.actionsVisible(true)
    }

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun waveComplete() {
        // TODO: This is where rewards are shown? Not sure the best spot
        vm.actionsVisible(true)

        val wave = runStateService.load().wave
        if (wave == 1 || wave % 4 == 0) {
            vm.chunkPlacementRequired(true)
        }
    }

    fun addChild(controller: Controller) = children.add(controller)

    fun startWave() {
        vm.actionsVisible(false)
        eventBus.enqueueEventSync(WaveStartedEvent())
    }
}
