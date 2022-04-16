package com.runt9.untdrl.view.duringRun.game.enemy

import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.enemy(enemy: EnemyViewModel, init: EnemyView.(S) -> Unit = {}) = uiComponent<S, EnemyController, EnemyView>({
    this.vm = enemy
}, init)

class EnemyController(private val eventBus: EventBus) : Controller {
    override lateinit var vm: EnemyViewModel
    override val view by lazy { EnemyView(this, vm) }

    override fun load() {
        eventBus.registerHandlers(this)
        vm.enemy.onHpChange {
            vm.hpPercent(currentHp / maxHp)
        }
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }
}
