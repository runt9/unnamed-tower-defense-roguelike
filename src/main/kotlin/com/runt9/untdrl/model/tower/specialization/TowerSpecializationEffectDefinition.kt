package com.runt9.untdrl.model.tower.specialization

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.specializationEffect.AttributeModifiersSpecializationEffect
import com.runt9.untdrl.service.specializationEffect.MinigunEffect
import com.runt9.untdrl.service.specializationEffect.ShotgunEffect
import com.runt9.untdrl.service.specializationEffect.TowerSpecializationEffect
import kotlin.reflect.KClass

interface TowerSpecializationEffectDefinition {
    val effectClass: KClass<out TowerSpecializationEffect>
}

class AttributeModifiersSpecialization(vararg val modifiers: AttributeModifier) : TowerSpecializationEffectDefinition {
    override val effectClass = AttributeModifiersSpecializationEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.sniperEffect() {
    definition = AttributeModifiersSpecialization(
        AttributeModifier(AttributeType.RANGE, percentModifier = 200f),
        AttributeModifier(AttributeType.DAMAGE, percentModifier = 200f),
        AttributeModifier(AttributeType.CRIT_CHANCE, percentModifier = 200f),
        AttributeModifier(AttributeType.CRIT_MULTI, percentModifier = 200f),
        AttributeModifier(AttributeType.ATTACK_SPEED, percentModifier = -75f)
    )
}

class MinigunSpecialization(val maxAttackSpeedBoost: Float, val attackSpeedBoostPerShot: Float, val attributeReduction: Float) : TowerSpecializationEffectDefinition {
    override val effectClass = MinigunEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.minigunEffect() {
    definition = MinigunSpecialization(500f, 50f, 50f)
}


class ShotgunSpecialization(val attributeReduction: Float) : TowerSpecializationEffectDefinition {
    override val effectClass = ShotgunEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.shotgunEffect() {
    definition = ShotgunSpecialization(25f)
}
