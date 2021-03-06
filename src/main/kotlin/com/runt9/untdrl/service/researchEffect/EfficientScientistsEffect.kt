package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.EfficientScientistsEffectDefinition
import com.runt9.untdrl.service.factionPassiveEffect.ResearchMultiplier
import com.runt9.untdrl.service.factionPassiveEffect.RndBudgetEffect
import com.runt9.untdrl.util.framework.event.EventBus

class EfficientScientistsEffect(
    override val eventBus: EventBus,
    private val definition: EfficientScientistsEffectDefinition,
    private val rndBudgetEffect: RndBudgetEffect
) : ResearchEffect {
    private val modifier: ResearchMultiplier = { definition.increasePct }

    override fun apply() {
        rndBudgetEffect.addResearchModifier(modifier)
    }

    override fun dispose() {
        super.dispose()
        rndBudgetEffect.removeResearchModifier(modifier)
    }
}
