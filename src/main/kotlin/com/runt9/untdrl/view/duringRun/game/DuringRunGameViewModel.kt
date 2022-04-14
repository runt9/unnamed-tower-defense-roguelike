package com.runt9.untdrl.view.duringRun.game

import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.game.chunk.ChunkViewModel
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileViewModel
import com.runt9.untdrl.view.duringRun.game.building.BuildingViewModel

class DuringRunGameViewModel : ViewModel() {
    val chunks = ListBinding<ChunkViewModel>()
    val enemies = ListBinding<EnemyViewModel>()
    val buildings = ListBinding<BuildingViewModel>()
    val projectiles = ListBinding<ProjectileViewModel>()
}
