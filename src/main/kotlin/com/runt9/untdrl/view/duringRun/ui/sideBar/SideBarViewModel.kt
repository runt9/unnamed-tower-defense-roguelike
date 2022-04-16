package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.loot.BuildingCore
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarViewModel : ViewModel() {
    val hp = Binding(25)
    val gold = Binding(0)
    val research = Binding(0)
    val wave = Binding(1)

    val chunkPlacementRequired = Binding(true)
    val actionsVisible = Binding(true)
    val availableBuildings = ListBinding<BuildingDefinition>()
    val canInteract = Binding(true)
    val selectedBuilding = Binding(BuildingDisplayViewModel())
    val consumables = ListBinding<Consumable>()
    val coreInventoryShown = Binding(false)
    val coreInventory = ListBinding<BuildingCore>()
}
