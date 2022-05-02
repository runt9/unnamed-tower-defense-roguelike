package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.faction.DividendsEffectDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.duringRun.Ticker
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.roundToInt

class DividendsEffect(
    override val eventBus: EventBus,
    private val tickerRegistry: TickerRegistry,
    definition: DividendsEffectDefinition,
    private val runStateService: RunStateService
) : ResearchEffect {
    private val stockMarket by lazyInject<StockMarketEffect>()
    var dividendPct = definition.dividendPct

    private lateinit var ticker: Ticker

    private fun gainDividend() {
        val dividendAmount = (stockMarket.investedGold * dividendPct).roundToInt()
        if (dividendAmount <= 0) return
        runStateService.update {
            gold += dividendAmount
        }
    }

    override fun apply() {
        Injector.bindSingleton(this)
        ticker = tickerRegistry.registerTimer(1f, action = ::gainDividend)
    }

    override fun dispose() {
        super.dispose()
        tickerRegistry.unregisterTicker(ticker)
        Injector.removeProvider(DividendsEffect::class.java)
    }
}
