package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.GoldPurseIncreaseDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus

class GoldPurseIncreaseEffect(
    override val eventBus: EventBus,
    private val definition: GoldPurseIncreaseDefinition,
    private val runStateService: RunStateService
) : ResearchEffect {
    override fun apply() {
        runStateService.update {
            goldPurseMax += definition.increaseAmt
        }
    }
}
