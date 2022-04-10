package com.runt9.untdrl.view.duringRun.ui.bottomBar

import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.event.NewTowerEvent
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.TowerCancelledEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.tower.definition.TowerDefinition
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
fun <S> KWidget<S>.bottomBar(init: BottomBarView.(S) -> Unit = {}) = uiComponent<S, BottomBarController, BottomBarView>(init = init)

class BottomBarController(private val runStateService: RunStateService, private val assets: AssetStorage, private val eventBus: EventBus) : Controller {
    override val vm = BottomBarViewModel()
    override val view = BottomBarView(this, vm)

    override fun load() {
        eventBus.registerHandlers(this)
        val runState = runStateService.load()
        vm.availableTowers(runState.availableTowers)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    @HandlesEvent
    fun runStateUpdated(event: RunStateUpdated) {
        vm.availableTowers(event.newState.availableTowers)
    }

    fun loadTexture(texture: UnitTexture): Texture = assets[texture.assetFile]

    fun addTower(tower: TowerDefinition) {
        if (!vm.canInteract.get()) return

        vm.canInteract(false)
        eventBus.enqueueEventSync(NewTowerEvent(tower))
    }

    @HandlesEvent(TowerPlacedEvent::class)
    suspend fun towerPlaced() = onRenderingThread {
        vm.canInteract(true)
    }

    @HandlesEvent(TowerCancelledEvent::class)
    suspend fun towerCancelled() = onRenderingThread {
        vm.canInteract(true)
    }
}
