package com.runt9.untdrl.view.duringRun.ui.loot

import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.INITIAL_GOLD_PURSE_MAX

class LootDialogViewModel : ViewModel() {
    val lootedGold = Binding(0)
    val lootedItems = ListBinding<LootItem>()

    val goldSelected = Binding(0)
    val maxGoldInPurse = Binding(INITIAL_GOLD_PURSE_MAX)
    val maxItemSelections = Binding(2)
    val selectedItems = ListBinding<LootItem>()
}
