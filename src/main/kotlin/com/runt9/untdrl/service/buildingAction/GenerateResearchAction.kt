package com.runt9.untdrl.service.buildingAction

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.action.GenerateResearchActionDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus

class GenerateResearchAction(
    private val definition: GenerateResearchActionDefinition,
    private val building: Building,
    override val eventBus: EventBus,
    private val runStateService: RunStateService
) : BuildingAction {
    private var timeBetweenGain = definition.timeBetweenGain
    private var amountPerTime = definition.amountPerTime
    private var goldPerTime = definition.goldCostPerTime

    private val generateTimer = Timer(timeBetweenGain)

    override suspend fun act(delta: Float) {
        generateTimer.tick(delta)

        if (generateTimer.isReady) {
            runStateService.update {
                if (gold < goldPerTime) return@update

                research += amountPerTime
                gold -= goldPerTime
            }

            building.gainXp(1)
            generateTimer.reset()
        }
    }

    override fun getStats(): Map<String, String> {
        return mapOf(
            "Time Between Ticks" to timeBetweenGain.displayDecimal(),
            "Research per Tick" to amountPerTime.toString(),
            "Gold Cost per Tick" to goldPerTime.toString()
        )
    }

    override fun levelUp(newLevel: Int) {
        // TODO: Decide if we want to actually have timeBetweenGain get better with levels
        amountPerTime += definition.amountPerTime
        goldPerTime += definition.goldCostPerTime
    }
}
