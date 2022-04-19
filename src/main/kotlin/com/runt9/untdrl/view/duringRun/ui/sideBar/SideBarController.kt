package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.mapToFloats
import com.runt9.untdrl.model.event.BuildingCancelledEvent
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.model.event.BuildingSelectedEvent
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import com.runt9.untdrl.view.duringRun.ui.sideBar.availableBuildings.SideBarAvailableBuildingsController
import com.runt9.untdrl.view.duringRun.ui.sideBar.building.SideBarBuildingController
import com.runt9.untdrl.view.duringRun.ui.sideBar.building.SideBarBuildingViewModel
import com.runt9.untdrl.view.duringRun.ui.sideBar.consumables.SideBarConsumablesController
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.sideBar(init: SideBarView.(S) -> Unit = {}) = uiComponent<S, SideBarController, SideBarView>(init = init)

class SideBarController(
    private val eventBus: EventBus,
    private val runStateService: RunStateService,
    private val buildingService: BuildingService
) : Controller {
    private val logger = unTdRlLogger()

    override val vm = SideBarViewModel()
    override val view = SideBarView(this, vm)
    private val children = mutableListOf<Controller>()

    private var selectedBuilding: Building? = null
    private var buildingCb: (suspend (Building) -> Unit)? = null

    override fun load() {
        eventBus.registerHandlers(this)
    }

    fun addChild(controller: Controller) = children.add(controller)

    @HandlesEvent(ChunkPlacedEvent::class)
    suspend fun chunkPlaced() = onRenderingThread {
        vm.actionsVisible(true)
        vm.chunkPlacementRequired(false)
    }

    @HandlesEvent(ChunkCancelledEvent::class)
    suspend fun chunkCancelled() = onRenderingThread {
        vm.actionsVisible(true)
    }

    @HandlesEvent(PrepareNextWaveEvent::class)
    suspend fun waveComplete() = onRenderingThread {
        vm.actionsVisible(true)

        val wave = runStateService.load().wave
        if (wave == 1 || wave % 4 == 0) {
            vm.chunkPlacementRequired(true)
        }
    }

    @HandlesEvent(BuildingPlacedEvent::class)
    suspend fun buildingPlaced() = onRenderingThread {
        vm.canInteract(true)
    }

    @HandlesEvent(BuildingCancelledEvent::class)
    suspend fun buildingCancelled() = onRenderingThread {
        vm.canInteract(true)
    }

    @HandlesEvent
    suspend fun buildingSelected(event: BuildingSelectedEvent) = onRenderingThread {
        val building = event.building
        val buildingVm = SideBarBuildingViewModel(false)
        buildingCb = { b -> onRenderingThread {
            buildingVm.apply {
                id(b.id)
                name(b.definition.name)
                xp(b.xp)
                xpToLevel(b.xpToLevel)
                level(b.level)
                attrs(b.attrs.mapToFloats())
                maxCores(b.maxCores)
                cores(b.cores)
            }
        }}

        buildingCb?.invoke(building)
        buildingService.onBuildingChange(building.id, buildingCb!!)
        vm.selectedBuilding(buildingVm)
        selectedBuilding = building
    }

    @HandlesEvent(CancelOpenItemsEvent::class)
    suspend fun deselectBuilding() = onRenderingThread {
        if (vm.selectedBuilding.get().empty && selectedBuilding == null) return@onRenderingThread

        buildingCb?.also { buildingService.removeBuildingChangeCb(vm.selectedBuilding.get().id.get(), it) }
        vm.selectedBuilding(SideBarBuildingViewModel())
        selectedBuilding = null
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

    fun startWave() {
        vm.actionsVisible(false)
        eventBus.enqueueEventSync(WaveStartedEvent())
    }

    fun removeDynamicSidebarControllers() {
        val controllers = children.filter {
            it is SideBarAvailableBuildingsController || it is SideBarConsumablesController || it is SideBarBuildingController
        }

        controllers.forEach(Controller::dispose)
        children.removeAll(controllers)
    }
}
