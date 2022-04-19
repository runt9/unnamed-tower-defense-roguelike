package com.runt9.untdrl.view.duringRun.ui.sideBar.availableBuildings

import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.event.BuildingCancelledEvent
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.NewBuildingEvent
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
fun <S> KWidget<S>.availableBuildings(init: SideBarAvailableBuildingsView.(S) -> Unit = {}) = uiComponent<S, SideBarAvailableBuildingsController, SideBarAvailableBuildingsView>({}, init)

class SideBarAvailableBuildingsController(private val eventBus: EventBus, private val runStateService: RunStateService, private val assets: AssetStorage) : Controller {
    override val vm = SideBarAvailableBuildingsViewModel()
    override val view = SideBarAvailableBuildingsView(this, vm)

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
        vm.availableBuildings(availableBuildings)
        vm.gold(gold)
    }

    fun loadTexture(texture: UnitTexture): Texture = assets[texture.assetFile]

    fun addBuilding(building: BuildingDefinition) {
        if (!canInteract || vm.gold.get() < building.goldCost) return

        canInteract = false
        eventBus.enqueueEventSync(CancelOpenItemsEvent())
        eventBus.enqueueEventSync(NewBuildingEvent(building))
    }

    @HandlesEvent(BuildingPlacedEvent::class)
    suspend fun buildingPlaced() = onRenderingThread {
        canInteract = true
    }

    @HandlesEvent(BuildingCancelledEvent::class)
    suspend fun buildingCancelled() = onRenderingThread {
        canInteract = true
    }
}
