package com.runt9.untdrl.model.building.action

import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.service.buildingAction.GenerateGoldAction

class GenerateGoldActionDefinition : BuildingActionDefinition {
    override val actionClass = GenerateGoldAction::class
}

fun BuildingDefinition.Builder.generateGold() {
    actionDefinition = GenerateGoldActionDefinition()
}
