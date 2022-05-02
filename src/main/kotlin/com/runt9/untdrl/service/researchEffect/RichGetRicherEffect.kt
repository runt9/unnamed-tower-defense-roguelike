package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.RichGetRicherEffectDefinition
import com.runt9.untdrl.service.factionPassiveEffect.ProfitCheck
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.displayMultiplier
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus

class RichGetRicherEffect(override val eventBus: EventBus, private val definition: RichGetRicherEffectDefinition) : ResearchEffect {
    private val logger = unTdRlLogger()
    private val stockMarket by lazyInject<StockMarketEffect>()

    private val profitCheck: ProfitCheck = ::profitCheck

    override fun apply() {
        stockMarket.addProfitCheck(profitCheck)
    }

    override fun dispose() {
        super.dispose()
        stockMarket.removeProfitCheck(profitCheck)
    }

    private fun profitCheck(profit: Float): Float {
        if (profit <= 1.00f) {
            logger.info { "Rich Get Richer: No profit, no bonus profit" }
            return profit
        }

        // NB: This is int division which is a floor, but that's actually what we want. 25 gold invested is
        // still 0.02x added, not 0.03x (as float division + roundToInt would be)
        val bonusProfit = (stockMarket.investedGold / definition.goldPerPct) / 100f
        logger.info { "Rich Get Richer: Profit ${profit.displayMultiplier()} with ${stockMarket.investedGold}g gains ${bonusProfit.displayMultiplier()}" }
        return profit + bonusProfit
    }
}
