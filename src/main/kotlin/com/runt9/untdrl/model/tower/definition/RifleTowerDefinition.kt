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
import com.runt9.untdrl.model.tower.action.ProjectileAttackActionDefinition
import com.runt9.untdrl.model.tower.specialization.AttributeModifiersSpecialization
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.MinigunEffect
import com.runt9.untdrl.service.specializationEffect.ShotgunEffect

val rifleTower = tower("Rifle Tower", PROTOTYPE_TOWER, 30) {
    +"A simple tower that shoots a single bullet at an enemy dealing Physical damage."

    +ProjectileAttackActionDefinition(PROJECTILE)

    RANGE(4f, 0.25f, FLAT)
    ATTACK_SPEED(1f, 0.05f, FLAT)
    DAMAGE(50f, 10f, PERCENT)
    CRIT_CHANCE(0.05f, 10f, PERCENT)
    CRIT_MULTI(1.5f, 0.1f, FLAT)

    damage(DamageType.PHYSICAL)

    // TODO: Contextual tooltips that fill in actual numbers
    specialization("Shotgun", PROTOTYPE_TOWER) {
        +"Tower fires 5 small projectiles in a cone in front of it. Base damage reduced by 25%. Range reduced by 25%. Bullets no longer follow targets."
        +ShotgunSpecialization(25f, 5)
    }

    specialization("Minigun", PROJECTILE) {
        +"Tower gains 50% increased attack speed after each shot, stacking up to a limit of 500%. Damage and Attack Speed reduced by 50%."
        +MinigunSpecialization(500f, 50f, 50f)
    }

    specialization("Sniper", GOLD_MINE) {
        +"Tower gains 200% increased range, damage, crit chance, and crit multiplier, however attack speed is reduced by 75%"
        sniperEffect(200f, -75f)
    }
}

fun TowerDefinition.Builder.SpecializationBuilder.sniperEffect(pctIncrease: Float, pctReduction: Float) {
    definition = AttributeModifiersSpecialization(
        AttributeModifier(RANGE, percentModifier = pctIncrease),
        AttributeModifier(DAMAGE, percentModifier = pctIncrease),
        AttributeModifier(CRIT_CHANCE, percentModifier = pctIncrease),
        AttributeModifier(CRIT_MULTI, percentModifier = pctIncrease),
        AttributeModifier(ATTACK_SPEED, percentModifier = pctReduction)
    )
}

class MinigunSpecialization(val maxAttackSpeedBoost: Float, val attackSpeedBoostPerShot: Float, val attributeReduction: Float) : TowerSpecializationEffectDefinition(MinigunEffect::class)
class ShotgunSpecialization(val attributeReduction: Float, val numProjectiles: Int) : TowerSpecializationEffectDefinition(ShotgunEffect::class)
