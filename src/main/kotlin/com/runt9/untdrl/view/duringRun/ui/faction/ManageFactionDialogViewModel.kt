package com.runt9.untdrl.view.duringRun.ui.faction

import com.runt9.untdrl.service.factionPassiveEffect.RiskTolerance
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class ManageFactionDialogViewModel : ViewModel() {
    val riskTolerance = Binding(RiskTolerance.LOW)
    val riskToleranceOptions = ListBinding<RiskTolerance>()
    val investmentPct = Binding(0.1f)
    val profitPct = Binding(0.1f)
    val minInvestPct = Binding(0.1f)
    val maxInvestPct = Binding(0.5f)
    val minProfitPct = Binding(0.1f)
    val maxProfitPct = Binding(0.5f)
}
