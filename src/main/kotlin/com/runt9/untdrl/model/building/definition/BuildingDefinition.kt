package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.BuildingActionDefinition
import com.runt9.untdrl.service.buildingAction.BuildingAction

interface BuildingDefinition {
    val id: Int
    val name: String
    val type: BuildingType
    val texture: UnitTexture
    val goldCost: Int
    val action: BuildingActionDefinition
}
