package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarViewModel : ViewModel() {
    val hp = Binding(25)
    val gold = Binding(0)
    val research = Binding(0)
    val wave = Binding(1)

    val chunkPlacementRequired = Binding(true)
    val actionsVisible = Binding(true)
    val availableTowers = ListBinding<TowerDefinition>()
    val canInteract = Binding(true)
    val selectedTower = Binding(TowerDisplayViewModel())
}