package com.runt9.untdrl.view.duringRun.ui.loot

import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class LootDialogViewModel : ViewModel() {
    val lootedGold = Binding(0)
    val lootedItems = ListBinding<LootItem>()

    val goldSelected = Binding(0)
    val maxGoldInPurse = Binding(50)
    val maxItemSelections = Binding(2)
    val selectedItems = ListBinding<LootItem>()
}
