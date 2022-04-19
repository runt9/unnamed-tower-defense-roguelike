package com.runt9.untdrl.model.building.action

import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.service.buildingAction.GenerateResearchAction

class GenerateResearchActionDefinition : BuildingActionDefinition {
    override val actionClass = GenerateResearchAction::class
}

fun BuildingDefinition.Builder.generateResearch() {
    actionDefinition = GenerateResearchActionDefinition()
}
