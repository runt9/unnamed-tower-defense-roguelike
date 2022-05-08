package com.runt9.untdrl.service.factionPassiveEffect

import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.displayMultiplier
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

typealias ProfitCheck = (Float) -> Float

class StockMarketEffect(
    override val eventBus: EventBus,
    private val runStateService: RunStateService,
    private val randomizer: RandomizerService
) : FactionPassiveEffect {
    class RiskTolerance(val name: String, var min: Float, var max: Float) {
        val riskRange = min..max

        override fun toString() = name
    }

    val lowTolerance = RiskTolerance("Low", 1f, 1.1f)
    val mediumTolerance = RiskTolerance("Medium", 0.9f, 1.25f)
    val highTolerance = RiskTolerance("High", 0.75f, 1.5f)
    val superHighTolerance = RiskTolerance("Super High", 0.5f, 2f)

    private val logger = unTdRlLogger()
    var riskTolerance = lowTolerance
    var riskToleranceOptions = listOf(lowTolerance, mediumTolerance, highTolerance)
    var investmentPct = 0.1f
    var minInvestPct = 0.1f
    var maxInvestPct = 0.5f
    var luckyProfit = false
    private val rndBudget by lazyInject<RndBudgetEffect>()

    var investedGold = 0
        private set

    var finalProfitMultiplier = 0f
        private set

    private val profitChecks = mutableListOf<ProfitCheck>()

    override fun init() {
        Injector.bindSingleton(this)
        super.init()
    }

    override fun dispose() {
        super.dispose()
        Injector.remove<StockMarketEffect>()
    }

    fun addProfitCheck(check: ProfitCheck) {
        profitChecks += check
    }

    fun removeProfitCheck(check: ProfitCheck) {
        profitChecks -= check
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
        val riskOutput = rollProfitCheck()
        finalProfitMultiplier = profitChecks.fold(riskOutput) { r, c -> c(r) }

        runStateService.update {
            val returnedGold = (investedGold * finalProfitMultiplier).roundToInt()
            val profit = returnedGold - investedGold
            if (profit > 0) {
                val (remainingProfit, addedResearch) = rndBudget.getResearchFromProfit(profit)
                val finalGold = investedGold + remainingProfit
                gold += finalGold
                researchAmount += addedResearch
                logger.info { "Stock Market profit [ Profit: ${finalProfitMultiplier.displayMultiplier()} | Turned ${investedGold}g into ${returnedGold}g | Made ${profit}g into ${remainingProfit}g and ${addedResearch}R ]" }
            } else {
                gold += returnedGold
                logger.info { "Stock Market did not profit, returned ${returnedGold}g" }
            }
        }
    }

    fun rollProfitCheck() = randomizer.range(riskTolerance.riskRange, luckyProfit)
}
