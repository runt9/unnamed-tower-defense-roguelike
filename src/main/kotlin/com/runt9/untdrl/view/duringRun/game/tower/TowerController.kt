package com.runt9.untdrl.view.duringRun.game.tower

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.runt9.untdrl.config.lazyInject
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.TowerCancelledEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.TowerSelectedEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.service.duringRun.IndexedGridGraph
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.InputMover
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.tower(tower: TowerViewModel, init: TowerView.(S) -> Unit = {}) = uiComponent<S, TowerController, TowerView>({
    this.vm = tower
    this.initTowerMover()
}, init)

class TowerController(private val eventBus: EventBus, private val towerService: TowerService, private val grid: IndexedGridGraph) : Controller {
    override lateinit var vm: TowerViewModel
    override val view by lazy { TowerView(this, vm) }
    private val input by lazyInject<InputMultiplexer>()
    private val camera by lazyInject<OrthographicCamera>()

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun initTowerMover() {
        vm.isSelected(true)

        input.addProcessor(InputMover(vm.tower, camera, eventBus, {
            vm.isValidPlacement(isValidTowerPlacement(vm.tower))
            vm.position(position.cpy())
        }, {
            if (!isValidTowerPlacement(vm.tower)) {
                return@InputMover false
            }

            input.removeProcessor(this)
            eventBus.enqueueEventSync(TowerPlacedEvent(vm.tower))
            return@InputMover true
        }) {
            input.removeProcessor(this)
            eventBus.enqueueEventSync(TowerCancelledEvent(vm.tower))
        })
    }

    private fun isValidTowerPlacement(tower: Tower) = towerService.isNoTowerPositionOverlap(tower) && grid.isEmptyTile(tower.position)

    @HandlesEvent
    fun towerSelected(event: TowerSelectedEvent) {
        vm.isSelected(event.tower == vm.tower)
    }

    @HandlesEvent(CancelOpenItemsEvent::class)
    fun cancelSelection() {
        vm.isSelected(false)
    }
}
