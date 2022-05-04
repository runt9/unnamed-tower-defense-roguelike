package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.BrainstormingEffectDefinition
import com.runt9.untdrl.service.duringRun.ResearchService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus

class BrainstormingEffect(
    override val eventBus: EventBus,
    private val definition: BrainstormingEffectDefinition,
    private val researchService: ResearchService,
    private val runStateService: RunStateService
) : ResearchEffect {
    override fun apply() {
        researchService.research.forEach { it.discount += definition.discountPct }
        runStateService.update {
            selectableResearchOptionCount++
        }
    }
}
