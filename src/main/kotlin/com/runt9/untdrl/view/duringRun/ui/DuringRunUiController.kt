package com.runt9.untdrl.view.duringRun.ui

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.controller.injectView
import com.runt9.untdrl.view.duringRun.ui.loot.LootDialogController
import ktx.async.onRenderingThread

class DuringRunUiController(private val eventBus: EventBus) : Controller {
    override val vm = DuringRunUiViewModel()
    override val view = injectView<DuringRunUiView>()
    private val children = mutableListOf<Controller>()

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        children.forEach(Disposable::dispose)
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun addChild(controller: Controller) = children.add(controller)

    @HandlesEvent
    suspend fun runStateUpdated(event: RunStateUpdated) = onRenderingThread {
        event.newState.apply {
            vm.relics(relics.toList())
        }
    }

    @HandlesEvent(WaveCompleteEvent::class)
    suspend fun waveComplete() = onRenderingThread {
        eventBus.enqueueShowDialog<LootDialogController>()
    }
}
