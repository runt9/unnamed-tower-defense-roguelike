package com.runt9.untdrl.view.duringRun.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.viewModel.plusAssign
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel
import ktx.assets.async.AssetStorage

class DuringRunGameController(private val eventBus: EventBus, private val assets: AssetStorage) : Controller {
    override val vm = DuringRunGameViewModel()
    override val view = DuringRunGameView(this, vm)
    private val children = mutableListOf<Controller>()

    override fun load() {
        eventBus.registerHandlers(this)
        addNewEnemy()
    }

    private fun addNewEnemy() {
        val enemy = EnemyViewModel(1, "testEnemy", assets[UnitTexture.PLAYER.assetFile], Vector2(1f, 1f), 0f)
        vm.enemies += enemy
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun clearChildren() {
        children.forEach(Disposable::dispose)
        children.clear()
    }

    fun addChild(controller: Controller) = children.add(controller)
}
