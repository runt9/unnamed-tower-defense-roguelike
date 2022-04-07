package com.runt9.untdrl.view.duringRun.game

import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.view.duringRun.GAME_HEIGHT
import com.runt9.untdrl.view.duringRun.GAME_WIDTH
import com.runt9.untdrl.view.duringRun.game.enemy.enemy
import com.runt9.untdrl.view.duringRun.game.projectile.projectile
import com.runt9.untdrl.view.duringRun.game.tower.tower
import ktx.actors.centerPosition
import ktx.scene2d.vis.floatingGroup

class DuringRunGameView(override val controller: DuringRunGameController, override val vm: DuringRunGameViewModel) : TableView(controller, vm) {
    private val logger = unTdRlLogger()

    override fun init() {
        val vm = vm
        val controller = controller

        setSize(GAME_WIDTH, GAME_HEIGHT)
        centerPosition()

        floatingGroup {
            vm.enemies.bind {
                this@DuringRunGameView.logger.info { "Updating all enemies" }
//                clear()
//                controller.clearChildren()
                vm.enemies.get().forEach { enemy -> enemy(enemy) { controller.addChild(this.controller) } }
            }

            vm.towers.bind {
                this@DuringRunGameView.logger.info { "Updating all towers" }
//                clear()
//                controller.clearChildren()
                vm.towers.get().forEach { tower -> tower(tower) { controller.addChild(this.controller) } }
            }

            vm.projectiles.bind {
                this@DuringRunGameView.logger.info { "Updating all projectiles" }
//                clear()
//                controller.clearChildren()
                vm.projectiles.get().forEach { projectile -> projectile(projectile) { controller.addChild(this.controller) } }
            }
        }.cell(row = true, grow = true)
    }
}
