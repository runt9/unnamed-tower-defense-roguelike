package com.runt9.untdrl.service.buildingAction

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.amountPerInterval
import com.runt9.untdrl.model.building.costPerInterval
import com.runt9.untdrl.model.building.gainInterval
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.roundToInt

class GenerateResearchAction(
    private val building: Building,
    override val eventBus: EventBus,
    private val buildingService: BuildingService,
    private val runStateService: RunStateService
) : BuildingAction {
    private val generateTimer = Timer(building.gainInterval)

    override suspend fun act(delta: Float) {
        if (building.gainInterval != generateTimer.targetTime) {
            generateTimer.targetTime = building.gainInterval
        }

        generateTimer.tick(delta)

        if (generateTimer.isReady) {
            runStateService.update {
                if (gold < building.costPerInterval) return@update

                researchAmount += building.amountPerInterval.roundToInt()
                gold -= building.costPerInterval.roundToInt()
            }

            buildingService.gainXp(building, 1)
            generateTimer.reset()
        }
    }
}
