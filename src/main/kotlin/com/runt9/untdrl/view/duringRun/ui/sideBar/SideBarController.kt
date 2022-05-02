package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.TowerCancelledEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.TowerSelectedEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.mapToFloats
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.controller.injectView
import com.runt9.untdrl.util.framework.ui.uiComponent
import com.runt9.untdrl.view.duringRun.ui.sideBar.availableTowers.SideBarAvailableTowersController
import com.runt9.untdrl.view.duringRun.ui.sideBar.consumables.SideBarConsumablesController
import com.runt9.untdrl.view.duringRun.ui.sideBar.tower.SideBarTowerController
import com.runt9.untdrl.view.duringRun.ui.sideBar.tower.SideBarTowerViewModel
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.sideBar(init: SideBarView.(S) -> Unit = {}) = uiComponent<S, SideBarController, SideBarView>(init = init)

class SideBarController(
    private val eventBus: EventBus,
    private val runStateService: RunStateService,
    private val towerService: TowerService
) : Controller {
    private val logger = unTdRlLogger()

    override val vm = SideBarViewModel()
    override val view = injectView<SideBarView>()
    private val children = mutableListOf<Controller>()

    private var selectedTower: Tower? = null
    private var towerCb: (suspend (Tower) -> Unit)? = null

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

    @HandlesEvent(TowerPlacedEvent::class)
    suspend fun towerPlaced() = onRenderingThread {
        vm.canInteract(true)
    }

    @HandlesEvent(TowerCancelledEvent::class)
    suspend fun towerCancelled() = onRenderingThread {
        vm.canInteract(true)
    }

    @HandlesEvent
    suspend fun towerSelected(event: TowerSelectedEvent) = onRenderingThread {
        val tower = event.tower
        val towerVm = SideBarTowerViewModel(false)
        towerCb = { b -> onRenderingThread {
            towerVm.apply {
                id(b.id)
                name(b.definition.name)
                xp(b.xp)
                xpToLevel(b.xpToLevel)
                level(b.level)
                attrs(b.attrs.mapToFloats())
                maxCores(b.maxCores)
                cores(b.cores.toList())
                canSpecialize(b.canSpecialize)
                hasSelectedSpecialization(b.appliedSpecialization != null)
                selectedSpecializationName(b.appliedSpecialization?.name ?: "")
                specializations(b.specializations.toList())
                canChangeTargetingMode(b.canChangeTargetingMode)
                targetingMode(b.targetingMode)
            }
        }}

        towerCb?.invoke(tower)
        towerService.onTowerChange(tower.id, towerCb!!)
        vm.selectedTower(towerVm)
        selectedTower = tower
    }

    @HandlesEvent(CancelOpenItemsEvent::class)
    suspend fun deselectTower() = onRenderingThread {
        if (vm.selectedTower.get().empty && selectedTower == null) return@onRenderingThread

        towerCb?.also { towerService.removeTowerChangeCb(vm.selectedTower.get().id.get(), it) }
        vm.selectedTower(SideBarTowerViewModel())
        selectedTower = null
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
            it is SideBarAvailableTowersController || it is SideBarConsumablesController || it is SideBarTowerController
        }

        controllers.forEach(Controller::dispose)
        children.removeAll(controllers)
    }
}
