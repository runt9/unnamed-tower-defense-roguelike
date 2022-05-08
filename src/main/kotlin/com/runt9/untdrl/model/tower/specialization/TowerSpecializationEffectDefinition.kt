package com.runt9.untdrl.model.tower.specialization

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.service.specializationEffect.AttributeModifiersSpecializationEffect
import com.runt9.untdrl.service.specializationEffect.TowerSpecializationEffect
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
abstract class TowerSpecializationEffectDefinition(val effectClass: KClass<out TowerSpecializationEffect>)
open class AttributeModifiersSpecialization(vararg val modifiers: AttributeModifier) : TowerSpecializationEffectDefinition(AttributeModifiersSpecializationEffect::class)
