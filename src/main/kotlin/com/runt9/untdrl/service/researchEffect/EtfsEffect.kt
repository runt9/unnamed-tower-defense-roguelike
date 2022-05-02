package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.factionPassiveEffect.ProfitCheck
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.displayMultiplier
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus

class EtfsEffect(override val eventBus: EventBus) : ResearchEffect {
    private val logger = unTdRlLogger()
    private val stockMarket by lazyInject<StockMarketEffect>()

    var lastWaveNegative = false

    private val profitCheck: ProfitCheck = ::profitCheck

    override fun apply() {
        stockMarket.addProfitCheck(profitCheck)
    }

    override fun dispose() {
        super.dispose()
        stockMarket.removeProfitCheck(profitCheck)
    }

    private fun profitCheck(profit: Float): Float {
        if (profit >= 1) {
            logger.info { "ETF Check: Positive profit, resetting last wave check" }
            lastWaveNegative = false
            return profit
        }

        if (!lastWaveNegative) {
            logger.info { "ETF Check: Negative profit, last wave check now negative" }
            lastWaveNegative = true
            return profit
        }

        logger.info { "ETF Check: Negative profit, last wave check was negative, trying for positive profit" }
        var finalProfit = profit
        while (finalProfit <= 1.00f) {
            finalProfit = stockMarket.rollProfitCheck()
        }

        logger.info { "ETF Check: Generated new positive profit ${finalProfit.displayMultiplier()}. Resetting last wave check" }
        lastWaveNegative = false
        return finalProfit
    }
}
