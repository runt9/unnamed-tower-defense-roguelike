package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.duringRun.LootService
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.framework.event.EventBus

class SpreadTheWealthEffect(
    override val eventBus: EventBus,
    private val lootService: LootService,
    private val stockMarket: StockMarketEffect
) : ResearchEffect {
    override fun apply() {
        lootService.addLootedGoldMultiplier {
            val profitPct = stockMarket.finalProfitMultiplier
            if (profitPct > 1f) profitPct - 1f else 0f
        }
    }
}
