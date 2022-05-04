package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.TextureDefinition.GOLD_MINE
import com.runt9.untdrl.model.TextureDefinition.PROTOTYPE_TOWER
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeType.AREA_OF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_CHANCE
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_MULTI
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.action.TowerActionDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.EmpMinesEffect
import com.runt9.untdrl.service.specializationEffect.ShellshockEffect
import com.runt9.untdrl.service.towerAction.MineThrowerAction

val mineThrower = tower("Mine Thrower", PROTOTYPE_TOWER, 150, false) {
    +"Places mines onto nearby paths that explode when contacted by an enemy, dealing Physical and Heat damage in an area."

    +MineThrowerActionDefinition(GOLD_MINE)

    RANGE(2f, 0.05f, FLAT)
    DAMAGE(125f, 10f, PERCENT)
    AREA_OF_EFFECT(1f, 5f, PERCENT)
    ATTACK_SPEED(0.25f, 0.025f, FLAT)
    CRIT_CHANCE(0.075f, 0.005f, FLAT)
    CRIT_MULTI(1.75f, 0.05f, FLAT)

    damage(DamageType.PHYSICAL, 0.5f)
    damage(DamageType.HEAT, 0.5f)

    specialization("Shellshock", TextureDefinition.ENEMY) {
        +"Mines gain a 50% chance to stun for 1s, but deal 75% reduced Damage and can no longer crit"
        +ShellshockSpecialization(75f, 0.5f, 1f)
    }

    specialization("EMP Mines", TextureDefinition.ENEMY) {
        +"Mines now deal 100% of base damage as Energy Damage, penetrate 20% of Energy resistance, and gain 10% increased Area of Effect, but have 25% reduced attack speed."
        +EmpMinesSpecialization(25f, 0.2f, 10f)
    }
}

class MineThrowerActionDefinition(val mineTexture: TextureDefinition) : TowerActionDefinition(MineThrowerAction::class)
class ShellshockSpecialization(val damageReduction: Float, val stunChance: Float, val stunDuration: Float) :
    TowerSpecializationEffectDefinition(ShellshockEffect::class)

class EmpMinesSpecialization(val attackSpeedReduction: Float, val energyPenetration: Float, val aoeGain: Float) :
    TowerSpecializationEffectDefinition(EmpMinesEffect::class)
