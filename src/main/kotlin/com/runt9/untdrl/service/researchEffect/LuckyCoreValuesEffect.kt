package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.service.duringRun.LootService
import com.runt9.untdrl.util.framework.event.EventBus

class LuckyCoreValuesEffect(
    override val eventBus: EventBus,
    private val lootService: LootService
) : ResearchEffect {
    override fun apply() {
        lootService.luckyCoreAttributes = true
    }
}
