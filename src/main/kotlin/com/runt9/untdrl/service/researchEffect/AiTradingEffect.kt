package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus

class AiTradingEffect(override val eventBus: EventBus) : ResearchEffect {
    private val stockMarket by lazyInject<StockMarketEffect>()

    override fun apply() {
        stockMarket.luckyProfit = true
    }
}
