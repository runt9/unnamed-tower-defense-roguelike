package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.framework.event.EventBus

class AiTradingEffect(override val eventBus: EventBus, private val stockMarket: StockMarketEffect) : ResearchEffect {
    override fun apply() {
        stockMarket.luckyProfit = true
    }
}
