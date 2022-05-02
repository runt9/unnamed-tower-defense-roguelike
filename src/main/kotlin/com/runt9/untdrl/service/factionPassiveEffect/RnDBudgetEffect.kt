package com.runt9.untdrl.service.factionPassiveEffect

import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

typealias ResearchModifier = (Float) -> Float

class RnDBudgetEffect(override val eventBus: EventBus) : FactionPassiveEffect {
    var profitPct = 0.1f
    var minProfitPct = 0.1f
    var maxProfitPct = 0.5f
    
    private val researchModifiers = mutableListOf<ResearchModifier>()

    override fun init() {
        Injector.bindSingleton(this)
    }

    override fun dispose() {
        Injector.removeProvider(RnDBudgetEffect::class.java)
        super.dispose()
    }

    fun addResearchModifier(check: ResearchModifier) {
        researchModifiers += check
    }

    fun removeResearchModifier(check: ResearchModifier) {
        researchModifiers -= check
    }

    fun getResearchFromProfit(profit: Int): Pair<Int, Int> {
        val research = floor(profit * profitPct)
        val remainingProfit = ceil(profit * (1 - profitPct))
        
        val finalResearch = researchModifiers.fold(research) { r, c -> c(r) }
        
        return Pair(remainingProfit.roundToInt(), finalResearch.roundToInt())
    }
}
