package com.runt9.untdrl.service.factionPassiveEffect

import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.factionPassiveEffect.RiskTolerance.HIGH
import com.runt9.untdrl.service.factionPassiveEffect.RiskTolerance.LOW
import com.runt9.untdrl.service.factionPassiveEffect.RiskTolerance.MEDIUM
import com.runt9.untdrl.util.ext.displayName
import com.runt9.untdrl.util.ext.lazyInject
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
    var riskTolerance = LOW
    var riskToleranceOptions = listOf(LOW, MEDIUM, HIGH)
    var investmentPct = 0.1f
    var minInvestPct = 0.1f
    var maxInvestPct = 0.5f
    private val rndBudget by lazyInject<RnDBudgetEffect>()

    private var investedGold = 0

    override fun init() {
        Injector.bindSingleton(this)
        super.init()
    }

    override fun dispose() {
        Injector.removeProvider(StockMarketEffect::class.java)
        super.dispose()
    }


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
            val profit = returnedGold - investedGold
            if (profit > 0) {
                val (remainingProfit, addedResearch) = rndBudget.getResearchFromProfit(profit)
                val finalGold = investedGold + remainingProfit
                gold += finalGold
                researchAmount += addedResearch
                logger.info { "Stock Market returned ${finalGold}g. Made ${profit}g that became ${remainingProfit}g and ${addedResearch}R" }
            } else {
                gold += returnedGold
                logger.info { "Stock Market did not profit, returned ${returnedGold}g" }
            }
        }
    }
}

enum class RiskTolerance(min: Float, max: Float) {
    LOW(1f, 1.1f),
    MEDIUM(0.9f, 1.25f),
    HIGH(0.75f, 1.5f),
    SUPER_HIGH(0.5f, 2f);

    val riskRange = min..max

    override fun toString() = displayName()
}
