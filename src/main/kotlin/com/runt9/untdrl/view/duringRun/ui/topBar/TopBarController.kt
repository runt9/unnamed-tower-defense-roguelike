package com.runt9.untdrl.view.duringRun.ui.topBar

import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import com.runt9.untdrl.view.duringRun.ui.menu.MenuDialogController
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.topBar(init: TopBarView.(S) -> Unit = {}) = uiComponent<S, TopBarController, TopBarView>(init = init)

class TopBarController(private val eventBus: EventBus) : Controller {
    override val vm = TopBarViewModel()
    override val view = TopBarView(this, vm)

    @HandlesEvent
    suspend fun runStateHandler(event: RunStateUpdated) = onRenderingThread {
        val newState = event.newState
        vm.apply {
            hp(newState.hp)
            gold(newState.gold)
            wave(newState.wave)
        }
    }

    @HandlesEvent(WaveStartedEvent::class)
    suspend fun waveStarted() = onRenderingThread {
        vm.isDuringWave(true)
    }

    @HandlesEvent(WaveCompleteEvent::class)
    suspend fun waveComplete() = onRenderingThread {
        vm.isDuringWave(false)
    }

    override fun load() {
        eventBus.registerHandlers(this)
    }

    fun menuButtonClicked() = eventBus.enqueueShowDialog<MenuDialogController>()

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }
}
