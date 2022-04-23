package com.runt9.untdrl.view.duringRun.game.building

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.mapToFloats
import com.runt9.untdrl.model.event.BuildingCancelledEvent
import com.runt9.untdrl.model.event.BuildingPlacedEvent
import com.runt9.untdrl.model.event.BuildingSelectedEvent
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.service.duringRun.IndexedGridGraph
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.InputMover
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.building(building: BuildingViewModel, init: BuildingView.(S) -> Unit = {}) = uiComponent<S, BuildingController, BuildingView>({
    this.vm = building
    this.initBuildingMover()
}, init)

class BuildingController(private val eventBus: EventBus, private val buildingService: BuildingService, private val grid: IndexedGridGraph) : Controller {
    override lateinit var vm: BuildingViewModel
    override val view by lazy { BuildingView(this, vm) }
    private val input by lazyInject<InputMultiplexer>()
    private val camera by lazyInject<OrthographicCamera>()

    val buildingCb: suspend (Building) -> Unit = { b -> onRenderingThread {
        vm.attrs(b.attrs.mapToFloats())
    }}

    override fun load() {
        eventBus.registerHandlers(this)
        buildingService.onBuildingChange(vm.building.id, buildingCb)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        buildingService.removeBuildingChangeCb(vm.building.id, buildingCb)
        super.dispose()
    }

    fun initBuildingMover() {
        if (vm.isValidPlacement.get()) return

        vm.isSelected(true)

        input.addProcessor(InputMover(vm.building, camera, eventBus, {
            vm.isValidPlacement(isValidBuildingPlacement(vm.building))
            vm.position(position.cpy())
        }, {
            if (!isValidBuildingPlacement(vm.building)) {
                return@InputMover false
            }

            input.removeProcessor(this)
            eventBus.enqueueEventSync(BuildingPlacedEvent(vm.building))
            vm.isSelected(false)
            return@InputMover true
        }) {
            input.removeProcessor(this)
            eventBus.enqueueEventSync(BuildingCancelledEvent(vm.building))
        })
    }

    private fun isValidBuildingPlacement(building: Building) = buildingService.isNoBuildingPositionOverlap(building) && grid.isEmptyTile(building.position)

    @HandlesEvent
    suspend fun buildingSelected(event: BuildingSelectedEvent) = onRenderingThread {
        vm.isSelected(event.building == vm.building)
    }

    @HandlesEvent(CancelOpenItemsEvent::class)
    suspend fun cancelSelection() = onRenderingThread {
        vm.isSelected(false)
    }
}
