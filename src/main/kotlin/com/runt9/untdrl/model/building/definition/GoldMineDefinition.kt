package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeType.AMOUNT_PER_INTERVAL
import com.runt9.untdrl.model.attribute.AttributeType.GAIN_INTERVAL
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.generateGold


val goldMine = building("Gold Mine", BuildingType.NON_COMBAT, UnitTexture.GOLD_MINE, 0) {
    generateGold()

    GAIN_INTERVAL(5f)
    AMOUNT_PER_INTERVAL(1f, 1f, AttributeModificationType.FLAT)
}
