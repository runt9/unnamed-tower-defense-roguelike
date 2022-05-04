package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.factionPassiveEffect.ResearchMultiplier
import com.runt9.untdrl.service.factionPassiveEffect.RndBudgetEffect
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.framework.event.EventBus

class RevenueSharingEffect(
    override val eventBus: EventBus,
    private val rndBudgetEffect: RndBudgetEffect,
    private val stockMarket: StockMarketEffect
) : ResearchEffect {
    private val modifier: ResearchMultiplier = {
        val profitPct = stockMarket.finalProfitMultiplier
        if (profitPct > 1f) profitPct - 1f else 0f
    }

    override fun apply() {
        rndBudgetEffect.addResearchModifier(modifier)
    }

    override fun dispose() {
        super.dispose()
        rndBudgetEffect.removeResearchModifier(modifier)
    }
}
