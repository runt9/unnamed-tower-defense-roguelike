package com.runt9.untdrl.model.building.action

import com.runt9.untdrl.service.buildingAction.GenerateResearchAction

abstract class GenerateResearchActionDefinition : BuildingActionDefinition {
    override val actionClass = GenerateResearchAction::class

    abstract val timeBetweenGain: Float
    abstract val amountPerTime: Int
    abstract val goldCostPerTime: Int
}
