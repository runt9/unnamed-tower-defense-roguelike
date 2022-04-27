package com.runt9.untdrl.view.duringRun.ui.sideBar.availableTowers

import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarAvailableTowersViewModel : ViewModel() {
    val availableTowers = ListBinding<TowerDefinition>()
    val gold = Binding(0)
}
