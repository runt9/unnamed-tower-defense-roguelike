package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.event.BuildingCancelledEvent
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.model.event.BuildingSelectedEvent
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.NewBuildingEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.model.loot.BuildingCore
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import com.runt9.untdrl.view.duringRun.ui.menu.MenuDialogController
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.sideBar(init: SideBarView.(S) -> Unit = {}) = uiComponent<S, SideBarController, SideBarView>(init = init)

class SideBarController(private val eventBus: EventBus, private val runStateService: RunStateService, private val assets: AssetStorage) : Controller {
    private val logger = unTdRlLogger()

    override val vm = SideBarViewModel()
    override val view = SideBarView(this, vm)

    private var selectedBuilding: Building? = null

    override fun load() {
        eventBus.registerHandlers(this)
        val runState = runStateService.load()
        vm.availableBuildings(runState.availableBuildings)
    }

    @HandlesEvent
    suspend fun runStateHandler(event: RunStateUpdated) = onRenderingThread {
        val newState = event.newState
        vm.apply {
            hp(newState.hp)
            research(newState.research)
            gold(newState.gold)
            wave(newState.wave)
            availableBuildings(newState.availableBuildings.toList())
            coreInventory(newState.cores.toList())
            consumables(newState.consumables.toList())
        }
    }

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
        vm.actionsVisible(true)
    }

    @HandlesEvent(BuildingCancelledEvent::class)
    suspend fun buildingCancelled() = onRenderingThread {
        vm.canInteract(true)
        vm.actionsVisible(true)
    }

    @HandlesEvent
    suspend fun buildingSelected(event: BuildingSelectedEvent) = onRenderingThread {
        val building = event.building
        val buildingVm = BuildingDisplayViewModel.fromBuilding(building)
        vm.selectedBuilding(buildingVm)
        selectedBuilding = building
    }

    @HandlesEvent(CancelOpenItemsEvent::class)
    suspend fun deselectBuilding() = onRenderingThread {
        vm.selectedBuilding(BuildingDisplayViewModel())
        selectedBuilding = null
    }

    fun menuButtonClicked() = eventBus.enqueueShowDialog<MenuDialogController>()

    override fun dispose() {
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

    fun loadTexture(texture: UnitTexture): Texture = assets[texture.assetFile]

    fun addBuilding(building: BuildingDefinition) {
        if (!vm.canInteract.get()) return

        vm.canInteract(false)
        vm.actionsVisible(true)
        eventBus.enqueueEventSync(CancelOpenItemsEvent())
        eventBus.enqueueEventSync(NewBuildingEvent(building))
    }

    fun useConsumable(consumable: Consumable) {
        logger.info { "Using consumable" }
        runStateService.update {
            consumables -= consumable
        }
        vm.consumables -= consumable
    }

    fun openCoreInventory() {
        vm.coreInventoryShown(true)
    }

    fun closeCoreInventory() {
        vm.coreInventoryShown(false)
    }

    fun placeCore(core: BuildingCore) {
        selectedBuilding?.cores?.plusAssign(core)
        vm.selectedBuilding.get().cores += core
        runStateService.update {
            cores -= core
            logger.info { "Removing core" }
        }
        closeCoreInventory()
    }
}
