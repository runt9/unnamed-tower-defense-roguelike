package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.PerformanceBonusDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.factionPassiveEffect.ResearchMultiplier
import com.runt9.untdrl.service.factionPassiveEffect.RndBudgetEffect
import com.runt9.untdrl.util.framework.event.EventBus

class PerformanceBonusEffect(
    override val eventBus: EventBus,
    private val definition: PerformanceBonusDefinition,
    private val rndBudgetEffect: RndBudgetEffect,
    private val runStateService: RunStateService
) : ResearchEffect {
    private val modifier: ResearchMultiplier = {
        runStateService.load().appliedResearch.size * definition.bonusPct
    }

    override fun apply() {
        rndBudgetEffect.addResearchModifier(modifier)
    }

    override fun dispose() {
        super.dispose()
        rndBudgetEffect.removeResearchModifier(modifier)
    }
}
