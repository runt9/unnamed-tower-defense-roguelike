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
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileController
import com.runt9.untdrl.view.duringRun.game.projectile.projectile
import com.runt9.untdrl.view.duringRun.game.building.BuildingController
import com.runt9.untdrl.view.duringRun.game.building.building
import ktx.scene2d.vis.floatingGroup

class DuringRunGameView(
    override val controller: DuringRunGameController,
    override val vm: DuringRunGameViewModel
) : TableView(controller, vm) {
    private val logger = unTdRlLogger()

    private val chunks = mutableListOf<ChunkController>()
    private val buildings = mutableListOf<BuildingController>()
    private val enemies = mutableListOf<EnemyController>()
    private val projectiles = mutableListOf<ProjectileController>()

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
            bindList(vm.buildings, this@DuringRunGameView.buildings) { building -> building(building).controller }
            bindList(vm.projectiles, this@DuringRunGameView.projectiles) { projectile -> projectile(projectile).controller }
        }.cell(row = true, grow = true)
    }
}
