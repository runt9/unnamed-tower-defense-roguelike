package com.runt9.untdrl.view.duringRun.ui.sideBar.consumables

import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarConsumablesViewModel : ViewModel() {
    val maxConsumables = Binding(3)
    val consumables = ListBinding<Consumable>()
}
