package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class NeuralNetworkEffect(override val eventBus: EventBus, private val runStateService: RunStateService) : ResearchEffect {
    private val stockMarket by lazyInject<StockMarketEffect>()

    override fun apply() {
        val currentWave = runStateService.load().wave
        val maxProfit = currentWave / 100f
        stockMarket.lowTolerance.max += maxProfit
        stockMarket.mediumTolerance.max += maxProfit
        stockMarket.highTolerance.max += maxProfit
        stockMarket.superHighTolerance.max += maxProfit
    }

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun prepareWave() {
        stockMarket.lowTolerance.max += 0.01f
        stockMarket.mediumTolerance.max += 0.01f
        stockMarket.highTolerance.max += 0.01f
        stockMarket.superHighTolerance.max += 0.01f
    }
}
