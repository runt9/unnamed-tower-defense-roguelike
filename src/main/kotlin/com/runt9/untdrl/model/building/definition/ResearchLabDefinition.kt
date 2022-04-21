package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.generateResearch

val researchLab = building("Research Lab", BuildingType.NON_COMBAT, UnitTexture.RESEARCH_LAB, 0) {
    generateResearch()

    AttributeType.GAIN_INTERVAL(5f)
    AttributeType.AMOUNT_PER_INTERVAL(1f, 1f, AttributeModificationType.FLAT)
    AttributeType.COST_PER_INTERVAL(1f)
}
