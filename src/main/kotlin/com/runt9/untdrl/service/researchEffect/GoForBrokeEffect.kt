package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.framework.event.EventBus

class GoForBrokeEffect(override val eventBus: EventBus, private val stockMarket: StockMarketEffect) : ResearchEffect {
    override fun apply() {
        stockMarket.maxInvestPct = 1f
        stockMarket.riskToleranceOptions += stockMarket.superHighTolerance

    }
}
