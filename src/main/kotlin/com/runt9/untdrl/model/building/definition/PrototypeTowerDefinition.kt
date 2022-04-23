package com.runt9.untdrl.model.building.definition

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
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.projectileAttack
import com.runt9.untdrl.model.building.upgrade.sniperEffect
import com.runt9.untdrl.model.damage.DamageType

val prototypeTower = building("Bullet Tower", BuildingType.TOWER, PROTOTYPE_TOWER, 30) {
    +"A simple tower that shoots a single bullet at an enemy."

    projectileAttack(PROJECTILE)

    RANGE(4f, 0.25f, FLAT)
    ATTACK_SPEED(1f, 0.05f, FLAT)
    DAMAGE(50f, 10f, PERCENT)
    CRIT_CHANCE(0.05f, 10f, PERCENT)
    CRIT_MULTI(1.5f, 0.1f, FLAT)

    damage(DamageType.PHYSICAL)

    // TODO: Contextual tooltips that fill in actual numbers
//    val incendiary = upgrade("Incendiary Bullets", ENEMY) {
//        +"Bullets set enemies ablaze, dealing (0.5x Base Damage) fire damage over 2 seconds."
//    }
//    val piercing = upgrade("Piercing Bullets", RESEARCH_LAB) {
//        +"Bullets pierce through the first enemy hit as well as penetrate 50% of Physical Resistance"
//    }
//
//    val shotgun = upgrade("Shotgun", PROTOTYPE_TOWER) {
//        +"Tower fires 5 small projectiles in a cone in front of it. Base damage reduced by 25%. Range reduced by 25%. Bullets no longer follow targets."
//    }
//
//    val minigun = upgrade("Minigun", PROJECTILE) {
//        exclusiveOf(shotgun)
//        +"Tower gains 50% increased attack speed after each shot, stacking up to a limit of 500%. Base damage reduced by 25%. Attack Speed reduced by 25%."
//    }

    val sniper = upgrade("Sniper", GOLD_MINE) {
//        exclusiveOf(shotgun, minigun)
        +"Tower gains 200% increased range, 200% increased base damage, and bullets pierce all enemies hit. Attack speed reduced by 75%"
        sniperEffect(AttributeModifier(RANGE, percentModifier = 200f), AttributeModifier(DAMAGE, percentModifier = 200f), AttributeModifier(ATTACK_SPEED, percentModifier = -75f))
    }

//    val dragonsBreath = upgrade("Dragon's Breath", ENEMY) {
//        dependsOn(shotgun, incendiary)
//        +"Tower fires a fan of 20 pieces of shrapnel in a cone, converting 50% of base damage to Fire Damage."
//    }
//    val vulcan = upgrade("Vulcan Cannon", PROJECTILE) {
//        dependsOn(minigun, piercing)
//        +"Bullets pierce all enemies hit and each hit lowers enemy's Physical Resistance by 10%"
//    }
}
