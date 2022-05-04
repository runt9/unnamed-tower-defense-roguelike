package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.TextureDefinition.ENEMY
import com.runt9.untdrl.model.TextureDefinition.PROJECTILE
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.action.TowerActionDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.NapalmEffect
import com.runt9.untdrl.service.specializationEffect.ToxicFumesEffect
import com.runt9.untdrl.service.towerAction.FlamethrowerAction

val flamethrower = tower("Flamethrower", ENEMY, 125) {
    +"Fires constant stream of flame in a small cone in front of it dealing Heat damage to all enemies inside. Has a 50% chance to Burn each enemy hit for 100% of base damage over 1.5s."

    +FlamethrowerActionDefinition(90f, 0.5f, 1.5f, 1f)

    RANGE(3f, 0.05f, FLAT)
    DAMAGE(50f, 5f, PERCENT)

    damage(DamageType.HEAT)

    specialization("Napalm", ENEMY) {
        +"Tower fires in a smaller angle and has 25% reduced damage, but chance to Burn is now 100% and deals 300% of base damage over 2s"
        +NapalmSpecialization(25f, 3f, 2f)
    }

    specialization("Toxic Fumes", PROJECTILE) {
        +"Tower now converts 50% of damage to Nature and gains a 20% chance to Poison each enemy hit for 25% of base damage over 1s. Damage reduced by 25%."
        +ToxicFumesSpecialization(25f, 0.5f, 0.25f, 1f, 0.25f)
    }
}

class FlamethrowerActionDefinition(val angle: Float, val burnChance: Float, val burnDuration: Float, val burnPctOfBase: Float) : TowerActionDefinition(FlamethrowerAction::class)
class NapalmSpecialization(
    val damageReduction: Float,
    val burnPctOfBase: Float,
    val burnDuration: Float
) : TowerSpecializationEffectDefinition(NapalmEffect::class)

class ToxicFumesSpecialization(
    val damageReduction: Float,
    val natureDamageConversion: Float,
    val poisonChance: Float,
    val poisonDuration: Float,
    val poisonPctOfBase: Float
) : TowerSpecializationEffectDefinition(ToxicFumesEffect::class)
