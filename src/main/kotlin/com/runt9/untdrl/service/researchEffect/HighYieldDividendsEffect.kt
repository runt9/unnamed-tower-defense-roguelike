package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.HighYieldDividendsEffectDefinition
import com.runt9.untdrl.util.framework.event.EventBus

class HighYieldDividendsEffect(
    override val eventBus: EventBus,
    private val definition: HighYieldDividendsEffectDefinition,
    private val dividends: DividendsEffect
) : ResearchEffect {
    override fun apply() {
        dividends.dividendPct = definition.dividendPct
    }
}
