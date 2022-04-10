package com.runt9.untdrl.view.duringRun.game

import com.runt9.untdrl.service.ChunkGenerator
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.view.duringRun.GAME_HEIGHT
import com.runt9.untdrl.view.duringRun.GAME_WIDTH
import com.runt9.untdrl.view.duringRun.game.chunk.chunk
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyController
import com.runt9.untdrl.view.duringRun.game.enemy.enemy
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileController
import com.runt9.untdrl.view.duringRun.game.projectile.projectile
import com.runt9.untdrl.view.duringRun.game.tower.tower
import ktx.scene2d.vis.floatingGroup

class DuringRunGameView(
    override val controller: DuringRunGameController,
    override val vm: DuringRunGameViewModel,
    private val chunkGenerator: ChunkGenerator
) : TableView(controller, vm) {
    private val logger = unTdRlLogger()

    private val enemies = mutableListOf<EnemyController>()
    private val projectiles = mutableListOf<ProjectileController>()

    override fun init() {
        val vm = vm
        val controller = controller

        setSize(GAME_WIDTH, GAME_HEIGHT)

        floatingGroup {
            vm.chunks.bindAdd { chunk ->
                chunk(chunk) { controller.addChild(this.controller) }
            }

            vm.enemies.bindAdd { enemy ->
                enemy(enemy) {
                    controller.addChild(this.controller)
                    this@DuringRunGameView.enemies += this.controller
                }
            }

            vm.enemies.bindRemove { enemy ->
                this@DuringRunGameView.enemies.find { it.vm == enemy }?.apply {
                    this@DuringRunGameView.enemies.remove(this)
                    this.dispose()
                }
            }

            vm.towers.bindAdd { tower ->
                tower(tower) { controller.addChild(this.controller) }
            }

            vm.projectiles.bindAdd { proj ->
                projectile(proj) {
                    controller.addChild(this.controller)
                    this@DuringRunGameView.projectiles += this.controller
                }
            }

            vm.projectiles.bindRemove { proj ->
                this@DuringRunGameView.projectiles.find { it.vm == proj }?.apply {
                    this@DuringRunGameView.projectiles.remove(this)
                    this.dispose()
                }
            }
        }.cell(row = true, grow = true)
    }
}
