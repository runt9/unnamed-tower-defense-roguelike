package com.runt9.untdrl.service.factionPassiveEffect

import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.random
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class StockMarketEffect(
    override val eventBus: EventBus,
    private val runStateService: RunStateService,
    private val randomizer: RandomizerService
) : FactionPassiveEffect {
    private val logger = unTdRlLogger()
    var riskTolerance = RiskTolerance.LOW
    var investmentPct = 0.1f

    private var investedGold = 0

    override fun apply() {}

    @HandlesEvent(WaveStartedEvent::class)
    fun waveStart() {
        runStateService.update {
            investedGold = (gold * investmentPct).roundToInt()
            gold -= investedGold
            logger.info { "Investing ${investedGold}g" }
        }
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveEnd() {
        val riskOutput = riskTolerance.riskRange.random(randomizer.rng)
        runStateService.update {
            val returnedGold = (investedGold * riskOutput).roundToInt()
            gold += returnedGold
            logger.info { "Stock Market returned ${returnedGold}g" }
        }
    }
}

enum class RiskTolerance(min: Float, max: Float) {
    LOW(1f, 1.1f),
    MEDIUM(0.9f, 1.25f),
    HIGH(0.75f, 1.5f);

    val riskRange = min..max
}
