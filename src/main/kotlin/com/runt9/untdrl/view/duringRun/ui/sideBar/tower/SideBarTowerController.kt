package com.runt9.untdrl.view.duringRun.ui.sideBar.tower

import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.tower.TargetingMode
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.sideBarTower(tower: SideBarTowerViewModel, init: SideBarTowerView.(S) -> Unit = {}) = uiComponent<S, SideBarTowerController, SideBarTowerView>({
    this.vm = tower
}, init)

class SideBarTowerController(
    private val runStateService: RunStateService,
    private val eventBus: EventBus,
    private val towerService: TowerService,
    private val assets: AssetStorage
) : Controller {
    override lateinit var vm: SideBarTowerViewModel
    override val view by lazy { SideBarTowerView(this, vm) }

    override fun load() {
        eventBus.registerHandlers(this)
        runStateService.load().apply {
            vm.coreInventory(cores)
        }
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

    fun placeCore(core: TowerCore) = launchOnRenderingThread {
        runStateService.update {
            cores -= core
        }
        towerService.addCore(vm.id.get(), core)
        closeCoreInventory()
    }

    fun targetingModeChange(targetingMode: TargetingMode) = launchOnRenderingThread {
        towerService.update(vm.id.get()) {
            this.targetingMode = targetingMode
        }
    }

    fun loadTexture(icon: UnitTexture): Texture = assets[icon.assetFile]

    fun applySpecialization(specialization: TowerSpecializationDefinition) {
        if (!vm.canSpecialize.get()) return

        launchOnRenderingThread {
            towerService.applySpecializationToTower(vm.id.get(), specialization)
        }
    }
}
