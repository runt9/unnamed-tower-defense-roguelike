package com.runt9.untdrl.view.duringRun.game.mine

import com.runt9.untdrl.model.tower.Mine
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class MineViewModel(val mine: Mine) : ViewModel() {
    val texture = Binding(mine.texture)
    val position = Binding(mine.position.cpy())
}
