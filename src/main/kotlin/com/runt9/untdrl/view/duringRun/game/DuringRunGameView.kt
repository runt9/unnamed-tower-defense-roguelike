package com.runt9.untdrl.view.duringRun.game

import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.view.duringRun.GAME_HEIGHT
import com.runt9.untdrl.view.duringRun.GAME_WIDTH
import com.runt9.untdrl.view.duringRun.game.enemy.enemy
import ktx.actors.centerPosition
import ktx.scene2d.vis.KFloatingGroup
import ktx.scene2d.vis.floatingGroup

class DuringRunGameView(override val controller: DuringRunGameController, override val vm: DuringRunGameViewModel) : TableView(controller, vm) {
    private val logger = unTdRlLogger()
    private var enemyPanel: KFloatingGroup? = null

    override fun init() {
        val vm = vm
        val controller = controller

        setSize(GAME_WIDTH, GAME_HEIGHT)
        centerPosition()

        enemyPanel = floatingGroup {
            vm.enemies.bind {
                this@DuringRunGameView.logger.info { "Updating all enemies" }
                clear()
                controller.clearChildren()
                vm.enemies.get().forEach { enemy -> enemy(enemy) { controller.addChild(this.controller) } }
            }
        }.cell(row = true, grow = true)
    }
}
