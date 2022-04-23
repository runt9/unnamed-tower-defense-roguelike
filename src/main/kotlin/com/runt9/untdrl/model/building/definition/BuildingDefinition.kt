package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.BuildingActionDefinition
import com.runt9.untdrl.model.building.upgrade.BuildingUpgradeDefinition
import com.runt9.untdrl.model.building.upgrade.BuildingUpgradeEffectDefinition
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType

interface BuildingDefinition {
    val name: String
    val description: String
    val type: BuildingType
    val texture: UnitTexture
    val goldCost: Int
    val action: BuildingActionDefinition
    val attrs: Map<AttributeType, BuildingAttributeDefinition>
    val upgrades: List<BuildingUpgradeDefinition>
    val damageTypes: List<DamageMap>

    class Builder {
        lateinit var actionDefinition: BuildingActionDefinition
        var description = ""
        val attrs = mutableMapOf<AttributeType, BuildingAttributeDefinition>()
        val upgrades = mutableListOf<BuildingUpgradeDefinition>()
        val damageTypes = mutableListOf<DamageMap>()

        operator fun AttributeType.invoke(baseValue: Float) = invoke(baseValue, 0f, AttributeModificationType.FLAT)
        operator fun AttributeType.invoke(baseValue: Float, growth: Float, growthType: AttributeModificationType) {
            attrs[this] = BuildingAttributeDefinition(this, baseValue, growth, growthType)
        }

        operator fun String.unaryPlus() {
            description = this
        }

        fun upgrade(name: String, icon: UnitTexture, builder: UpgradeBuilder.() -> Unit = {}): BuildingUpgradeDefinition {
            val upgradeBuilder = UpgradeBuilder()
            upgradeBuilder.builder()

            val upgrade = object : BuildingUpgradeDefinition {
                override val icon = icon
                override val name = name
                override val description = upgradeBuilder.description
                override val effect = upgradeBuilder.definition
                override val dependsOn = upgradeBuilder.dependsOn.toList()
                override val exclusiveOf = upgradeBuilder.exclusiveOf.toList()
            }

            upgrades += upgrade
            return upgrade
        }

        fun damage(type: DamageType, pctOfBase: Float = 1f, penetration: Float = 0f) {
            damageTypes += DamageMap(type, pctOfBase, penetration)
        }

        class UpgradeBuilder {
            internal val dependsOn = mutableListOf<BuildingUpgradeDefinition>()
            internal val exclusiveOf = mutableListOf<BuildingUpgradeDefinition>()
            internal var description = ""
            internal lateinit var definition: BuildingUpgradeEffectDefinition

            fun dependsOn(vararg upgrades: BuildingUpgradeDefinition) = dependsOn.addAll(upgrades)
            fun exclusiveOf(vararg upgrades: BuildingUpgradeDefinition) = exclusiveOf.addAll(upgrades)
            operator fun String.unaryPlus() {
                description = this
            }
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
        override val description = builder.description
        override val type = type
        override val texture = texture
        override val goldCost = goldCost
        override val action = builder.actionDefinition
        override val attrs = builder.attrs.toMap()
        override val upgrades = builder.upgrades.toList()
        override val damageTypes = builder.damageTypes.toList()
    }
}
