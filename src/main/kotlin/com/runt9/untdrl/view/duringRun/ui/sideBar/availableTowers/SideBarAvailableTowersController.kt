package com.runt9.untdrl.view.duringRun.ui.sideBar.availableTowers

import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.model.event.TowerCancelledEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.NewTowerEvent
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.availableTowers(init: SideBarAvailableTowersView.(S) -> Unit = {}) = uiComponent<S, SideBarAvailableTowersController, SideBarAvailableTowersView>({}, init)

class SideBarAvailableTowersController(private val eventBus: EventBus, private val runStateService: RunStateService, private val assets: AssetStorage) : Controller {
    override val vm = SideBarAvailableTowersViewModel()
    override val view = SideBarAvailableTowersView(this, vm)

    private var canInteract = true

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
        vm.availableTowers(availableTowers)
        vm.gold(gold)
    }

    fun loadTexture(texture: UnitTexture): Texture = assets[texture.assetFile]

    fun addTower(tower: TowerDefinition) {
        if (!canInteract || vm.gold.get() < tower.goldCost) return

        canInteract = false
        eventBus.enqueueEventSync(CancelOpenItemsEvent())
        eventBus.enqueueEventSync(NewTowerEvent(tower))
    }

    @HandlesEvent(TowerPlacedEvent::class)
    suspend fun towerPlaced() = onRenderingThread {
        canInteract = true
    }

    @HandlesEvent(TowerCancelledEvent::class)
    suspend fun towerCancelled() = onRenderingThread {
        canInteract = true
    }
}
