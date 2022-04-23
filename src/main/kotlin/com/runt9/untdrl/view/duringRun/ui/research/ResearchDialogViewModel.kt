package com.runt9.untdrl.view.duringRun.ui.research

import com.runt9.untdrl.model.research.ResearchDefinition
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import com.runt9.untdrl.view.duringRun.REROLL_COST

class ResearchDialogViewModel : ViewModel() {
    val gold = Binding(0)
    val researchAmount = Binding(0)
    val rerollCost = Binding(REROLL_COST)
    val research = ListBinding<ResearchDefinition>()
}
