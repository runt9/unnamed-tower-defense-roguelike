package com.runt9.untdrl.view.duringRun.game.tower

import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.tower(tower: TowerViewModel, init: TowerView.(S) -> Unit = {}) = uiComponent<S, TowerController, TowerView>({
    this.vm = tower
}, init)

class TowerController(private val eventBus: EventBus) : Controller {
    override lateinit var vm: TowerViewModel
    override val view by lazy { TowerView(this, vm) }

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }
}
