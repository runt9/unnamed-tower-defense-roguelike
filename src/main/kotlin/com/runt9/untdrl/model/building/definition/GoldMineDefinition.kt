package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.GenerateGoldActionDefinition

object GoldMineDefinition : BuildingDefinition {
    override val name = "Gold Mine"
    override val type = BuildingType.NON_COMBAT
    override val texture = UnitTexture.GOLD_MINE
    override val goldCost = 50

    override val action = object : GenerateGoldActionDefinition() {
        override val timeBetweenGain = 1f
        override val amountPerTime = 1
    }
}
