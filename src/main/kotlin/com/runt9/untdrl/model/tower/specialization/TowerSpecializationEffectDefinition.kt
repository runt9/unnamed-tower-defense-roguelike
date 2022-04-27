package com.runt9.untdrl.model.tower.specialization

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.specializationEffect.TowerSpecializationEffect
import com.runt9.untdrl.service.specializationEffect.SniperEffect
import kotlin.reflect.KClass

interface TowerSpecializationEffectDefinition {
    val effectClass: KClass<out TowerSpecializationEffect>
}

class SniperDefinition(val modifiers: List<AttributeModifier>) : TowerSpecializationEffectDefinition {
    override val effectClass = SniperEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.sniperEffect(vararg modifiers: AttributeModifier) {
    definition = SniperDefinition(modifiers.toList())
}
