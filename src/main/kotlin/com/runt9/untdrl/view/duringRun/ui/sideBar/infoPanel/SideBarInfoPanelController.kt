package com.runt9.untdrl.view.duringRun.ui.sideBar.infoPanel

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import com.runt9.untdrl.view.duringRun.ui.menu.MenuDialogController
import com.runt9.untdrl.view.duringRun.ui.shop.ShopDialogController
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.infoPanel(init: SideBarInfoPanelView.(S) -> Unit = {}) = uiComponent<S, SideBarInfoPanelController, SideBarInfoPanelView>({}, init)

class SideBarInfoPanelController(private val eventBus: EventBus, private val runStateService: RunStateService) : Controller {
    override val vm = SideBarInfoPanelViewModel()
    override val view = SideBarInfoPanelView(this, vm)

    override fun load() {
        eventBus.registerHandlers(this)
        runStateService.load().applyNewState()
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    @HandlesEvent
    suspend fun runStateUpdated(event: RunStateUpdated) = onRenderingThread { event.newState.applyNewState() }

    private fun RunState.applyNewState() {
        vm.hp(hp)
        vm.gold(gold)
        vm.research(research)
        vm.wave(wave)
    }

    fun menuButtonClicked() = eventBus.enqueueShowDialog<MenuDialogController>()
    fun shopButtonClicked() = eventBus.enqueueShowDialog<ShopDialogController>()
    fun researchButtonClicked() = Unit
}
