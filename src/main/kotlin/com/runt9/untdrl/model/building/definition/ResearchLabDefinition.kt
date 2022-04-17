package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.GenerateResearchActionDefinition

object ResearchLabDefinition : BuildingDefinition {
    override val name = "Research Lab"
    override val type = BuildingType.NON_COMBAT
    override val texture = UnitTexture.RESEARCH_LAB
    override val goldCost = 100

    override val action = object : GenerateResearchActionDefinition() {
        override val timeBetweenGain = 1f
        override val amountPerTime = 1
        override val goldCostPerTime = 1
    }
}
