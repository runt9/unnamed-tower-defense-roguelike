package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.UnitTexture.GOLD_MINE
import com.runt9.untdrl.model.UnitTexture.PROJECTILE
import com.runt9.untdrl.model.UnitTexture.PROTOTYPE_TOWER
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
import com.runt9.untdrl.model.tower.specialization.sniperEffect

val prototypeTower = tower("Bullet Tower", PROTOTYPE_TOWER, 30) {
    +"A simple tower that shoots a single bullet at an enemy."

    projectileAttack(PROJECTILE)

    RANGE(4f, 0.25f, FLAT)
    ATTACK_SPEED(1f, 0.05f, FLAT)
    DAMAGE(50f, 10f, PERCENT)
    CRIT_CHANCE(0.05f, 10f, PERCENT)
    CRIT_MULTI(1.5f, 0.1f, FLAT)

    damage(DamageType.PHYSICAL)

    // TODO: Contextual tooltips that fill in actual numbers
//    val shotgun = specialization("Shotgun", PROTOTYPE_TOWER) {
//        +"Tower fires 5 small projectiles in a cone in front of it. Base damage reduced by 25%. Range reduced by 25%. Bullets no longer follow targets."
//    }
//
//    val minigun = specialization("Minigun", PROJECTILE) {
//        exclusiveOf(shotgun)
//        +"Tower gains 50% increased attack speed after each shot, stacking up to a limit of 500%. Base damage reduced by 25%. Attack Speed reduced by 25%."
//    }

    val sniper = specialization("Sniper", GOLD_MINE) {
        +"Tower gains 200% increased range, 200% increased base damage, and bullets pierce all enemies hit. Attack speed reduced by 75%"
        sniperEffect(AttributeModifier(RANGE, percentModifier = 200f), AttributeModifier(DAMAGE, percentModifier = 200f), AttributeModifier(ATTACK_SPEED, percentModifier = -75f))
    }
}
