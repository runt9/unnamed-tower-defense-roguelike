package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.factionPassiveEffect.RndBudgetEffect
import com.runt9.untdrl.util.framework.event.EventBus

class ScienceFirstApproachEffect(override val eventBus: EventBus, private val rndBudgetEffect: RndBudgetEffect) : ResearchEffect {
    override fun apply() {
        rndBudgetEffect.maxProfitPct = 1f
    }
}
