package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.DontRepeatYourselfDefinition
import com.runt9.untdrl.model.research.ResearchItem
import com.runt9.untdrl.service.duringRun.ResearchService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus

class DontRepeatYourselfEffect(
    override val eventBus: EventBus,
    private val definition: DontRepeatYourselfDefinition,
    private val researchService: ResearchService,
    private val runStateService: RunStateService
) : ResearchEffect {
    private val appliedDiscounts = mutableMapOf<ResearchItem, Float>()

    override fun apply() {
        researchService.onResearchApplied {
            runStateService.update {
                availableResearch.forEach { item ->
                    val previouslyApplied = appliedDiscounts.getOrPut(item) { 0f }
                    if (previouslyApplied >= definition.discountCap) return@forEach

                    item.discount += definition.discountPct
                    appliedDiscounts.merge(item, definition.discountPct, Float::plus)
                }
            }
        }
    }
}
