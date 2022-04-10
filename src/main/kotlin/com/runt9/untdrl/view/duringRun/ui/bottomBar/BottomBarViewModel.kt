package com.runt9.untdrl.view.duringRun.ui.bottomBar

import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class BottomBarViewModel : ViewModel() {
    val availableTowers = ListBinding<TowerDefinition>()
    val canInteract = Binding(true)
}
