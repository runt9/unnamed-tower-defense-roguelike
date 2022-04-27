package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeType

data class TowerAttributeDefinition(
    val type: AttributeType,
    val baseValue: Float,
    val growthPerLevel: Float,
    val growthType: AttributeModificationType
)
