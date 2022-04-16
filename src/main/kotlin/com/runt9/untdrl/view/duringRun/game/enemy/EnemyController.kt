package com.runt9.untdrl.view.duringRun.game.enemy

import com.runt9.untdrl.model.event.EnemyHpChanged
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.async.onRenderingThread
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.enemy(enemy: EnemyViewModel, init: EnemyView.(S) -> Unit = {}) = uiComponent<S, EnemyController, EnemyView>({
    this.vm = enemy
}, init)

class EnemyController(private val eventBus: EventBus) : Controller {
    override lateinit var vm: EnemyViewModel
    override val view by lazy { EnemyView(this, vm) }

    @HandlesEvent
    suspend fun hpChanged(event: EnemyHpChanged) = onRenderingThread {
        val enemy = event.enemy
        if (enemy != vm.enemy) return@onRenderingThread

        vm.hpPercent(enemy.currentHp / enemy.maxHp)
    }

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }
}
