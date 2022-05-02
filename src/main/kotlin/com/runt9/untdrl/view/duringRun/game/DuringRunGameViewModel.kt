package com.runt9.untdrl.view.duringRun.game

import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.game.chunk.ChunkViewModel
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel
import com.runt9.untdrl.view.duringRun.game.mine.MineViewModel
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileViewModel
import com.runt9.untdrl.view.duringRun.game.tower.TowerViewModel

class DuringRunGameViewModel : ViewModel() {
    val chunks = ListBinding<ChunkViewModel>()
    val enemies = ListBinding<EnemyViewModel>()
    val towers = ListBinding<TowerViewModel>()
    val projectiles = ListBinding<ProjectileViewModel>()
    val mines = ListBinding<MineViewModel>()
}
