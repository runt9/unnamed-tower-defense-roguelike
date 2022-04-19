package com.runt9.untdrl.view.duringRun.ui.shop

import com.runt9.untdrl.model.loot.Shop
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.REROLL_COST

class ShopDialogViewModel : ViewModel() {
    val gold = Binding(0)
    val rerollCost = Binding(REROLL_COST)
    val shop = Binding(Shop())
}
