package com.runt9.untdrl.view.duringRun.ui.sideBar.consumables

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.consumables(init: SideBarConsumablesView.(S) -> Unit = {}) = uiComponent<S, SideBarConsumablesController, SideBarConsumablesView>({}, init)

class SideBarConsumablesController(private val eventBus: EventBus, private val runStateService: RunStateService) : Controller {
    override val vm = SideBarConsumablesViewModel()
    override val view = SideBarConsumablesView(this, vm)

    override fun load() {
        eventBus.registerHandlers(this)
        runStateService.load().applyNewState()
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }

    @HandlesEvent
    fun runStateUpdated(event: RunStateUpdated) {
        event.newState.applyNewState()
    }

    private fun RunState.applyNewState() {
        vm.consumables(consumables)
    }

    fun useConsumable(consumable: Consumable) {
        runStateService.update {
            consumables -= consumable
        }
        vm.consumables -= consumable
    }
}
