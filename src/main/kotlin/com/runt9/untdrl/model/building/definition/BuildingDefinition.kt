package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.BuildingActionDefinition

interface BuildingDefinition {
    val name: String
    val type: BuildingType
    val texture: UnitTexture
    val goldCost: Int
    val action: BuildingActionDefinition
    val attrs: Map<AttributeType, BuildingAttributeDefinition>

    class Builder {
        lateinit var actionDefinition: BuildingActionDefinition
        val attrs = mutableMapOf<AttributeType, BuildingAttributeDefinition>()

        operator fun AttributeType.invoke(baseValue: Float) = invoke(baseValue, 0f, AttributeModificationType.FLAT)
        operator fun AttributeType.invoke(baseValue: Float, growth: Float, growthType: AttributeModificationType) {
            attrs[this] = BuildingAttributeDefinition(this, baseValue, growth, growthType)
        }
    }
}

fun building(
    name: String,
    type: BuildingType,
    texture: UnitTexture,
    goldCost: Int,
    init: BuildingDefinition.Builder.() -> Unit
): BuildingDefinition {
    val builder = BuildingDefinition.Builder()
    builder.init()

    return object : BuildingDefinition {
        override val name = name
        override val type = type
        override val texture = texture
        override val goldCost = goldCost
        override val action = builder.actionDefinition
        override val attrs = builder.attrs.toMap()
    }
}
