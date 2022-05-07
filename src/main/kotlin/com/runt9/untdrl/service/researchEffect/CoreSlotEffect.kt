package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class CoreSlotEffect(
    override val eventBus: EventBus,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower { t -> t.maxCores++ }
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        event.tower.maxCores++
    }
}
