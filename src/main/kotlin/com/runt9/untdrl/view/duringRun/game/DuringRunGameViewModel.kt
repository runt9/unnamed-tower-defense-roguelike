package com.runt9.untdrl.view.duringRun.game

import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel

class DuringRunGameViewModel : ViewModel() {
    val enemies = Binding(listOf<EnemyViewModel>())
}
