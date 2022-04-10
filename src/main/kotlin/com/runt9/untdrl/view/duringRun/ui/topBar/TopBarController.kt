package com.runt9.untdrl.view.duringRun.ui.topBar

import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import com.runt9.untdrl.view.duringRun.ui.menu.MenuDialogController

@Scene2dDsl
fun <S> KWidget<S>.topBar(init: TopBarView.(S) -> Unit = {}) = uiComponent<S, TopBarController, TopBarView>(init = init)

class TopBarController(private val eventBus: EventBus) : Controller {
    override val vm = TopBarViewModel()
    override val view = TopBarView(this, vm)

    @HandlesEvent
    suspend fun runStateHandler(event: RunStateUpdated) = onRenderingThread {
        val newState = event.newState
        vm.apply {
            gold(newState.gold)
            wave(newState.wave)
            isDuringWave(newState.waveActive)
        }
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
