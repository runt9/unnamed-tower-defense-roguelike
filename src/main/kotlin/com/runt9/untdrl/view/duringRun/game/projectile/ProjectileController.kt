package com.runt9.untdrl.view.duringRun.game.projectile

import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.projectile(projectile: ProjectileViewModel, init: ProjectileView.(S) -> Unit = {}) = uiComponent<S, ProjectileController, ProjectileView>({
    this.vm = projectile
}, init)

class ProjectileController(private val eventBus: EventBus) : Controller {
    override lateinit var vm: ProjectileViewModel
    override val view by lazy { ProjectileView(this, vm) }

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }
}
