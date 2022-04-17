package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.ui.sideBar.building.SideBarBuildingViewModel

class SideBarViewModel : ViewModel() {

    val chunkPlacementRequired = Binding(true)
    val actionsVisible = Binding(true)
    val canInteract = Binding(true)
    val selectedBuilding = Binding(SideBarBuildingViewModel())
    val consumables = ListBinding<Consumable>()

}
