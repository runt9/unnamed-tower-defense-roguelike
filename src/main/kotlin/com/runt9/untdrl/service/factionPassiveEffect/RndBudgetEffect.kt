package com.runt9.untdrl.service.factionPassiveEffect

import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

typealias ResearchMultiplier = (profit: Int) -> Float

class RndBudgetEffect(override val eventBus: EventBus) : FactionPassiveEffect {
    var profitPct = 0.1f
    var minProfitPct = 0.1f
    var maxProfitPct = 0.5f
    
    private val researchMultipliers = mutableListOf<ResearchMultiplier>()

    override fun init() {
        Injector.bindSingleton(this)
    }

    override fun dispose() {
        Injector.removeProvider(RndBudgetEffect::class.java)
        super.dispose()
    }

    fun addResearchModifier(check: ResearchMultiplier) {
        researchMultipliers += check
    }

    fun removeResearchModifier(check: ResearchMultiplier) {
        researchMultipliers -= check
    }

    fun getResearchFromProfit(profit: Int): Pair<Int, Int> {
        val research = floor(profit * profitPct)
        val remainingProfit = ceil(profit * (1 - profitPct))
        
        val totalResearchMultiplier = researchMultipliers.map { it(profit) }.sum()
        val finalResearch = research * (1 + totalResearchMultiplier)
        
        return Pair(remainingProfit.roundToInt(), finalResearch.roundToInt())
    }
}
