package com.runt9.untdrl.view.duringRun.game.enemy

import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class EnemyViewModel(val enemy: Enemy) : ViewModel() {
    val texture = enemy.definition.texture
    val position = Binding(enemy.position.cpy())
    val rotation = Binding(enemy.rotation)
    val hpPercent = Binding(1f)
}
