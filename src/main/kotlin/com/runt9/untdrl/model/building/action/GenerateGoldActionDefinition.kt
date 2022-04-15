package com.runt9.untdrl.model.building.action

import com.runt9.untdrl.service.buildingAction.GenerateGoldAction

abstract class GenerateGoldActionDefinition : BuildingActionDefinition {
    override val actionClass = GenerateGoldAction::class

    abstract val timeBetweenGain: Float
    abstract val amountPerTime: Int
}
