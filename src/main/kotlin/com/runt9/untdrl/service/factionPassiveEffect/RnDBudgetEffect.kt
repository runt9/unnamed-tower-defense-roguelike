package com.runt9.untdrl.service.factionPassiveEffect

import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.ceil
import kotlin.math.floor

class RnDBudgetEffect(override val eventBus: EventBus) : FactionPassiveEffect {
    var profitPct = 0.1f
    var minProfitPct = 0.1f
    var maxProfitPct = 0.5f

    override fun init() {
        Injector.bindSingleton(this)
    }

    override fun dispose() {
        Injector.removeProvider(RnDBudgetEffect::class.java)
        super.dispose()
    }

    fun getResearchFromProfit(profit: Int): Pair<Int, Int> {
        val research = profit * profitPct
        val remainingProfit = profit * (1 - profitPct)
        return Pair(ceil(remainingProfit).toInt(), floor(research).toInt())
    }
}
