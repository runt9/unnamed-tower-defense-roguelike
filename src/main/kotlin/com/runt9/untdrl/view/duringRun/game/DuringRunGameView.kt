package com.runt9.untdrl.view.duringRun.game

import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.GAME_HEIGHT
import com.runt9.untdrl.view.duringRun.GAME_WIDTH
import com.runt9.untdrl.view.duringRun.game.chunk.ChunkController
import com.runt9.untdrl.view.duringRun.game.chunk.chunk
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyController
import com.runt9.untdrl.view.duringRun.game.enemy.enemy
import com.runt9.untdrl.view.duringRun.game.mine.MineController
import com.runt9.untdrl.view.duringRun.game.mine.mine
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileController
import com.runt9.untdrl.view.duringRun.game.projectile.projectile
import com.runt9.untdrl.view.duringRun.game.tower.TowerController
import com.runt9.untdrl.view.duringRun.game.tower.tower
import ktx.scene2d.vis.floatingGroup

class DuringRunGameView(
    override val controller: DuringRunGameController,
    override val vm: DuringRunGameViewModel
) : TableView() {
    private val logger = unTdRlLogger()

    private val chunks = mutableListOf<ChunkController>()
    private val towers = mutableListOf<TowerController>()
    private val enemies = mutableListOf<EnemyController>()
    private val projectiles = mutableListOf<ProjectileController>()
    private val mines = mutableListOf<MineController>()

    override fun init() {
        val vm = vm
        val controller = controller

        setSize(GAME_WIDTH, GAME_HEIGHT)

        fun <VM : ViewModel, C : Controller> bindList(vmList: ViewModel.ListBinding<VM>, viewList: MutableList<C>, controllerGetter: (VM) -> C) {
            vmList.bindAdd { item ->
                val itemController = controllerGetter(item)
                controller.addChild(itemController)
                viewList += itemController
            }

            vmList.bindRemove { item ->
                viewList.find { it.vm == item }?.apply {
                    viewList.remove(this)
                    this.dispose()
                }
            }
        }

        floatingGroup {
            bindList(vm.chunks, this@DuringRunGameView.chunks) { chunk -> chunk(chunk).controller }
            bindList(vm.enemies, this@DuringRunGameView.enemies) { enemy -> enemy(enemy).controller }
            bindList(vm.towers, this@DuringRunGameView.towers) { tower -> tower(tower).controller }
            bindList(vm.projectiles, this@DuringRunGameView.projectiles) { projectile -> projectile(projectile).controller }
            bindList(vm.mines, this@DuringRunGameView.mines) { mine -> mine(mine).controller }
        }.cell(row = true, grow = true)
    }
}
