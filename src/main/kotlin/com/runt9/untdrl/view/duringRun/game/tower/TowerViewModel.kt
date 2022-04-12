package com.runt9.untdrl.view.duringRun.game.tower

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class TowerViewModel(val tower: Tower) : ViewModel() {
    val position = Binding(tower.position.cpy())
    val rotation = Binding(tower.rotation)
    val isSelected = Binding(false)
    val range = Binding(tower.range)
    val isValidPlacement = Binding(false)
    val texture = tower.texture
}
