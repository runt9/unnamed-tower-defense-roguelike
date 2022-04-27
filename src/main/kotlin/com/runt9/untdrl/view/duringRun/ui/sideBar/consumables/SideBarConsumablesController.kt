package com.runt9.untdrl.view.duringRun.ui.sideBar.consumables

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.consumables(init: SideBarConsumablesView.(S) -> Unit = {}) = uiComponent<S, SideBarConsumablesController, SideBarConsumablesView>({}, init)

class SideBarConsumablesController(private val eventBus: EventBus, private val runStateService: RunStateService) : Controller {
    private val logger = unTdRlLogger()
    override val vm = SideBarConsumablesViewModel()
    override val view = SideBarConsumablesView(this, vm)

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
        vm.maxConsumables(consumableSlots)
        vm.consumables(consumables)
    }

    fun useConsumable(consumable: Consumable) {
        if (!consumable.action.canApply()) return

        logger.info { "Applying consumable action" }
        consumable.action.apply()

        logger.info { "Updating runState to remove consumable" }
        runStateService.update {
            consumables -= consumable
            logger.info { "Inside update block removing consumable" }
        }
        logger.info { "Removing consumable from vm" }
        vm.consumables -= consumable
    }
}
