package com.runt9.untdrl.view.duringRun.ui.faction

import com.runt9.untdrl.service.factionPassiveEffect.RnDBudgetEffect
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.ui.controller.DialogController
import com.runt9.untdrl.util.framework.ui.controller.injectView

class ManageFactionDialogController : DialogController() {
    override val vm = ManageFactionDialogViewModel()
    override val view = injectView<ManageFactionDialogView>()

    private val stockMarket by lazyInject<StockMarketEffect>()
    private val rndBudget by lazyInject<RnDBudgetEffect>()

    override fun load() {
        vm.investmentPct(stockMarket.investmentPct)
        vm.riskTolerance(stockMarket.riskTolerance)
        vm.riskToleranceOptions(stockMarket.riskToleranceOptions)
        vm.profitPct(rndBudget.profitPct)
        vm.minInvestPct(stockMarket.minInvestPct)
        vm.maxInvestPct(stockMarket.maxInvestPct)
        vm.minProfitPct(rndBudget.minProfitPct)
        vm.maxProfitPct(rndBudget.maxProfitPct)
    }

    fun done() {
        stockMarket.investmentPct = vm.investmentPct.get()
        stockMarket.riskTolerance = vm.riskTolerance.get()
        rndBudget.profitPct = vm.profitPct.get()
        hide()
    }

    fun cancel() {
        hide()
    }
}
