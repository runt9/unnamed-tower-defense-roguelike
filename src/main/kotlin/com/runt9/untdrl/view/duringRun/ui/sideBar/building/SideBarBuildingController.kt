package com.runt9.untdrl.view.duringRun.ui.sideBar.building

import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.sideBarBuilding(building: SideBarBuildingViewModel, init: SideBarBuildingView.(S) -> Unit = {}) = uiComponent<S, SideBarBuildingController, SideBarBuildingView>({
    this.vm = building
}, init)

class SideBarBuildingController(private val runStateService: RunStateService, private val eventBus: EventBus) : Controller {
    override lateinit var vm: SideBarBuildingViewModel
    override val view by lazy { SideBarBuildingView(this, vm) }

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    @HandlesEvent
    suspend fun runStateHandler(event: RunStateUpdated) = onRenderingThread {
        val newState = event.newState
        vm.apply {
            coreInventory(newState.cores)
        }
    }

    fun openCoreInventory() {
        vm.coreInventoryShown(true)
    }

    fun closeCoreInventory() {
        vm.coreInventoryShown(false)
    }

    fun placeCore(core: TowerCore) {
        vm.cores += core
        runStateService.update {
            cores -= core
        }
        closeCoreInventory()
    }
}
