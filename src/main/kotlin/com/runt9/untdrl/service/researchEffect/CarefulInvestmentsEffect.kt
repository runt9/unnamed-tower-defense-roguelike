package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus

class CarefulInvestmentsEffect(override val eventBus: EventBus, private val stockMarket: StockMarketEffect) : ResearchEffect {
    override fun apply() {
        stockMarket.mediumTolerance.min = 0.95f
        stockMarket.highTolerance.min = 0.875f
        stockMarket.superHighTolerance.min = 0.75f
    }
}
