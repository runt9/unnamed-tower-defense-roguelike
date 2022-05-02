package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.EfficientScientistsEffectDefinition
import com.runt9.untdrl.service.factionPassiveEffect.ResearchModifier
import com.runt9.untdrl.service.factionPassiveEffect.RnDBudgetEffect
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus

class EfficientScientistsEffect(override val eventBus: EventBus, private val definition: EfficientScientistsEffectDefinition) : ResearchEffect {
    private val rndBudgetEffect by lazyInject<RnDBudgetEffect>()

    private val modifier: ResearchModifier = { it + (it * definition.increasePct) }

    override fun apply() {
        rndBudgetEffect.addResearchModifier(modifier)
    }

    override fun dispose() {
        super.dispose()
        rndBudgetEffect.removeResearchModifier(modifier)
    }
}
