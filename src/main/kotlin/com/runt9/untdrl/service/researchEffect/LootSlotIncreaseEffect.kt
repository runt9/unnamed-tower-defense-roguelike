package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus

class LootSlotIncreaseEffect(
    override val eventBus: EventBus,
    private val runStateService: RunStateService
) : ResearchEffect {
    override fun apply() {
        runStateService.update {
            lootItemMax++
        }
    }
}
