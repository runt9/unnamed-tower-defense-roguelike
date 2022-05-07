package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.EfficientGrowthDefinition
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus

class EfficientGrowthEffect(
    override val eventBus: EventBus,
    private val definition: EfficientGrowthDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.addGlobalAttrGrowthModifier(definition.growthPct)
    }
}
