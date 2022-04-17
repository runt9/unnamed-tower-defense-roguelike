package com.runt9.untdrl.view.duringRun.ui.sideBar.availableBuildings

import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarAvailableBuildingsViewModel : ViewModel() {
    val availableBuildings = ListBinding<BuildingDefinition>()
    val gold = Binding(0)
    val canInteract = Binding(true)
}
