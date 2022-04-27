package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.action.TowerActionDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition

interface TowerDefinition {
    val name: String
    val description: String
    val texture: UnitTexture
    val goldCost: Int
    val action: TowerActionDefinition
    val attrs: Map<AttributeType, TowerAttributeDefinition>
    val specializations: List<TowerSpecializationDefinition>
    val damageTypes: List<DamageMap>

    class Builder {
        lateinit var actionDefinition: TowerActionDefinition
        var description = ""
        val attrs = mutableMapOf<AttributeType, TowerAttributeDefinition>()
        val specializations = mutableListOf<TowerSpecializationDefinition>()
        val damageTypes = mutableListOf<DamageMap>()

        operator fun AttributeType.invoke(baseValue: Float) = invoke(baseValue, 0f, AttributeModificationType.FLAT)
        operator fun AttributeType.invoke(baseValue: Float, growth: Float, growthType: AttributeModificationType) {
            attrs[this] = TowerAttributeDefinition(this, baseValue, growth, growthType)
        }

        operator fun String.unaryPlus() {
            description = this
        }

        fun specialization(name: String, icon: UnitTexture, builder: SpecializationBuilder.() -> Unit = {}): TowerSpecializationDefinition {
            val specializationBuilder = SpecializationBuilder()
            specializationBuilder.builder()

            val specialization = object : TowerSpecializationDefinition {
                override val icon = icon
                override val name = name
                override val description = specializationBuilder.description
                override val effect = specializationBuilder.definition
                override val dependsOn = specializationBuilder.dependsOn.toList()
            }

            specializations += specialization
            return specialization
        }

        fun damage(type: DamageType, pctOfBase: Float = 1f, penetration: Float = 0f) {
            damageTypes += DamageMap(type, pctOfBase, penetration)
        }

        class SpecializationBuilder {
            internal val dependsOn = mutableListOf<TowerSpecializationDefinition>()
            internal var description = ""
            internal lateinit var definition: TowerSpecializationEffectDefinition

            operator fun String.unaryPlus() {
                description = this
            }
        }
    }
}

fun tower(
    name: String,
    texture: UnitTexture,
    goldCost: Int,
    init: TowerDefinition.Builder.() -> Unit
): TowerDefinition {
    val builder = TowerDefinition.Builder()
    builder.init()

    return object : TowerDefinition {
        override val name = name
        override val description = builder.description
        override val texture = texture
        override val goldCost = goldCost
        override val action = builder.actionDefinition
        override val attrs = builder.attrs.toMap()
        override val specializations = builder.specializations.toList()
        override val damageTypes = builder.damageTypes.toList()
    }
}
