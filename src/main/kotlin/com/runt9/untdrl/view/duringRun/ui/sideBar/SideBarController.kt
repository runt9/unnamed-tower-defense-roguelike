package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.NewTowerEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.TowerCancelledEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.TowerSelectedEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
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
    override val vm = SideBarViewModel()
    override val view = SideBarView(this, vm)

    override fun load() {
        eventBus.registerHandlers(this)
        val runState = runStateService.load()
        vm.availableTowers(runState.availableTowers)
    }

    @HandlesEvent
    suspend fun runStateHandler(event: RunStateUpdated) = onRenderingThread {
        val newState = event.newState
        vm.apply {
            hp(newState.hp)
            research(newState.research)
            gold(newState.gold)
            wave(newState.wave)
            availableTowers(newState.availableTowers)
        }
    }

    @HandlesEvent(ChunkPlacedEvent::class)
    fun chunkPlaced() {
        vm.actionsVisible(true)
        vm.chunkPlacementRequired(false)
    }

    @HandlesEvent(ChunkCancelledEvent::class)
    fun chunkCancelled() {
        vm.actionsVisible(true)
    }

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun waveComplete() {
        vm.actionsVisible(true)

        val wave = runStateService.load().wave
        if (wave == 1 || wave % 4 == 0) {
            vm.chunkPlacementRequired(true)
        }
    }

    @HandlesEvent(TowerPlacedEvent::class)
    suspend fun towerPlaced() = onRenderingThread {
        vm.canInteract(true)
        vm.actionsVisible(true)
    }

    @HandlesEvent(TowerCancelledEvent::class)
    suspend fun towerCancelled() = onRenderingThread {
        vm.canInteract(true)
        vm.actionsVisible(true)
    }

    @HandlesEvent
    suspend fun towerSelected(event: TowerSelectedEvent) = onRenderingThread {
        val tower = event.tower
        val towerVm = TowerDisplayViewModel.fromTower(tower)
        vm.selectedTower(towerVm)
    }

    @HandlesEvent(CancelOpenItemsEvent::class)
    suspend fun deselectTower() = onRenderingThread {
        vm.selectedTower(TowerDisplayViewModel())
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

    fun addTower(tower: TowerDefinition) {
        if (!vm.canInteract.get()) return

        vm.canInteract(false)
        vm.actionsVisible(true)
        eventBus.enqueueEventSync(CancelOpenItemsEvent())
        eventBus.enqueueEventSync(NewTowerEvent(tower))
    }
}
