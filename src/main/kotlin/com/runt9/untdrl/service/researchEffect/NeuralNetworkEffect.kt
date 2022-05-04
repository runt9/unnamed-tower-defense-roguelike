package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.faction.NeuralNetworkEffectDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class NeuralNetworkEffect(
    override val eventBus: EventBus,
    private val runStateService: RunStateService,
    private val definition: NeuralNetworkEffectDefinition
) : ResearchEffect {
    private val stockMarket by lazyInject<StockMarketEffect>()

    override fun apply() {
        val currentWave = runStateService.load().wave
        val maxProfit = currentWave * definition.profitPctPerWave
        stockMarket.lowTolerance.max += maxProfit
        stockMarket.mediumTolerance.max += maxProfit
        stockMarket.highTolerance.max += maxProfit
        stockMarket.superHighTolerance.max += maxProfit
    }

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun prepareWave() {
        stockMarket.lowTolerance.max += definition.profitPctPerWave
        stockMarket.mediumTolerance.max += definition.profitPctPerWave
        stockMarket.highTolerance.max += definition.profitPctPerWave
        stockMarket.superHighTolerance.max += definition.profitPctPerWave
    }
}
