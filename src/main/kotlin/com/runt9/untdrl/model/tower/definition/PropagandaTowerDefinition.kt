package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.TextureDefinition.PROJECTILE
import com.runt9.untdrl.model.TextureDefinition.PROTOTYPE_TOWER
import com.runt9.untdrl.model.TextureDefinition.RESEARCH_LAB
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.BUFF_DEBUFF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.tower.action.AttributeBuffActionDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.MentalDisruptionEffect
import com.runt9.untdrl.service.specializationEffect.RiseToTheOccasionEffect
import com.runt9.untdrl.service.specializationEffect.SayItLouderEffect

val propagandaTower = tower("Propaganda Tower", RESEARCH_LAB, 100) {
    +"Buffs nearby towers, granting them 25% increased Damage and Attack Speed."

    actionDefinition = AttributeBuffActionDefinition(
        AttributeModifier(DAMAGE, percentModifier = 25f),
        AttributeModifier(ATTACK_SPEED, percentModifier = 25f),
    )

    RANGE(2f, 0.05f, FLAT)
    BUFF_DEBUFF_EFFECT(0f, 0.01f, FLAT)

    specialization("Say it Louder", PROTOTYPE_TOWER) {
        +"Tower now only provides 15% increased Damage and Attack Speed, but now slows all enemies in range by 25%"
        sayItLouder(10f, 0.25f)
    }

    specialization("Rise to the Occasion", PROJECTILE) {
        +"No longer provides a flat bonus and no longer has Buff/Debuff Effect. Instead towers in range gain 1% increased Damage and Attack Speed per second."
        riseToTheOccasion(1f)
    }

    specialization("Mental Disruption", RESEARCH_LAB) {
        +"Tower now only provides 10% increased Damage and Attack Speed, but affected Towers now gain an additional 10% of base damage as Mystic Damage."
        mentalDisruption(15f, 0.1f)
    }
}

class SayItLouderDefinition(val buffReduction: Float, val slowPercentage: Float) : TowerSpecializationEffectDefinition {
    override val effectClass = SayItLouderEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.sayItLouder(buffReduction: Float, slowPercentage: Float) {
    definition = SayItLouderDefinition(buffReduction, slowPercentage)
}

class RiseToTheOccasionDefinition(val stackPerSecond: Float) : TowerSpecializationEffectDefinition {
    override val effectClass = RiseToTheOccasionEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.riseToTheOccasion(stackPerSecond: Float) {
    definition = RiseToTheOccasionDefinition(stackPerSecond)
}

class MentalDisruptionDefinition(val buffReduction: Float, val damagePct: Float) : TowerSpecializationEffectDefinition {
    override val effectClass = MentalDisruptionEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.mentalDisruption(buffReduction: Float, damagePct: Float) {
    definition = MentalDisruptionDefinition(buffReduction, damagePct)
}
