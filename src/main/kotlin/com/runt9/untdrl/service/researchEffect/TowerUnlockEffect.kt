package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.research.TowerUnlockEffectDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus

class TowerUnlockEffect(
    override val eventBus: EventBus,
    private val runStateService: RunStateService,
    private val definition: TowerUnlockEffectDefinition
) : ResearchEffect {
    override fun apply() {
        runStateService.update {
            availableTowers += definition.towerDef
        }
    }
}
