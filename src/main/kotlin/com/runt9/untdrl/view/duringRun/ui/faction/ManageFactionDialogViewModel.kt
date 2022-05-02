package com.runt9.untdrl.view.duringRun.ui.faction

import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class ManageFactionDialogViewModel : ViewModel() {
    val riskTolerance = Binding(StockMarketEffect.RiskTolerance("Low", 1f, 1.1f))
    val riskToleranceOptions = ListBinding<StockMarketEffect.RiskTolerance>()
    val investmentPct = Binding(0.1f)
    val profitPct = Binding(0.1f)
    val minInvestPct = Binding(0.1f)
    val maxInvestPct = Binding(0.5f)
    val minProfitPct = Binding(0.1f)
    val maxProfitPct = Binding(0.5f)
}
