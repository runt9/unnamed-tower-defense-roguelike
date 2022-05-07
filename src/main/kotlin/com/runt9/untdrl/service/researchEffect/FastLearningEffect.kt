package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.FastLearningDefinition
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus

class FastLearningEffect(
    override val eventBus: EventBus,
    private val definition: FastLearningDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.addGlobalXpModifier(definition.xpPercent)
    }
}
