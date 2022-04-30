package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.TextureDefinition.GOLD_MINE
import com.runt9.untdrl.model.TextureDefinition.PROJECTILE
import com.runt9.untdrl.model.TextureDefinition.PROTOTYPE_TOWER
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_CHANCE
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_MULTI
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.action.projectileAttack
import com.runt9.untdrl.model.tower.specialization.AttributeModifiersSpecialization
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.MinigunEffect
import com.runt9.untdrl.service.specializationEffect.ShotgunEffect

val rifleTower = tower("Rifle Tower", PROTOTYPE_TOWER, 30) {
    +"A simple tower that shoots a single bullet at an enemy dealing Physical damage."

    projectileAttack(PROJECTILE)

    RANGE(4f, 0.25f, FLAT)
    ATTACK_SPEED(1f, 0.05f, FLAT)
    DAMAGE(50f, 10f, PERCENT)
    CRIT_CHANCE(0.05f, 10f, PERCENT)
    CRIT_MULTI(1.5f, 0.1f, FLAT)

    damage(DamageType.PHYSICAL)

    // TODO: Contextual tooltips that fill in actual numbers
    specialization("Shotgun", PROTOTYPE_TOWER) {
        +"Tower fires 5 small projectiles in a cone in front of it. Base damage reduced by 25%. Range reduced by 25%. Bullets no longer follow targets."
        shotgunEffect()
    }

    specialization("Minigun", PROJECTILE) {
        +"Tower gains 50% increased attack speed after each shot, stacking up to a limit of 500%. Damage and Attack Speed reduced by 50%."
        minigunEffect()
    }

    specialization("Sniper", GOLD_MINE) {
        +"Tower gains 200% increased range, damage, crit chance, and crit multiplier, however attack speed is reduced by 75%"
        sniperEffect()
    }
}

fun TowerDefinition.Builder.SpecializationBuilder.sniperEffect() {
    definition = AttributeModifiersSpecialization(
        AttributeModifier(RANGE, percentModifier = 200f),
        AttributeModifier(DAMAGE, percentModifier = 200f),
        AttributeModifier(CRIT_CHANCE, percentModifier = 200f),
        AttributeModifier(CRIT_MULTI, percentModifier = 200f),
        AttributeModifier(ATTACK_SPEED, percentModifier = -75f)
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
