package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.UnitTexture.ENEMY
import com.runt9.untdrl.model.UnitTexture.GOLD_MINE
import com.runt9.untdrl.model.UnitTexture.PROTOTYPE_TOWER
import com.runt9.untdrl.model.UnitTexture.RESEARCH_LAB
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeType.AREA_OF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.action.projectileAttack
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.MissileSwarmEffect
import com.runt9.untdrl.service.specializationEffect.NapalmCannonEffect

val rocketTower = tower("Rocket Tower", GOLD_MINE, 75) {
    +"Fires a rocket at an enemy that explodes on contact dealing AoE Physical and Heat damage."

    projectileAttack(RESEARCH_LAB, speed = 4f)

    RANGE(5f, 0.2f, FLAT)
    ATTACK_SPEED(0.5f, 0.025f, FLAT)
    DAMAGE(100f, 10f, PERCENT)
    AREA_OF_EFFECT(0.75f, 5f, PERCENT)

    damage(DamageType.PHYSICAL, 0.5f)
    damage(DamageType.HEAT, 0.5f)

    specialization("Missile Swarm Tower", PROTOTYPE_TOWER) {
        +"Tower now fires 3 missiles, but loses 50% Damage and AoE."
        missileSwarmEffect()
    }

    specialization("Napalm Cannon", ENEMY) {
        +"Tower loses 50% Damage, but gains 25% AoE, converts all damage to Heat, and Burns all enemies hit for 50% of the hit damage over 2s"
        napalmCannonEffect()
    }
}

class MissileSwarmSpecialization(val attributeReduction: Float) : TowerSpecializationEffectDefinition {
    override val effectClass = MissileSwarmEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.missileSwarmEffect() {
    definition = MissileSwarmSpecialization(50f)
}

class NapalmCannonSpecialization(val damageReduction: Float, val aoeGain: Float) : TowerSpecializationEffectDefinition {
    override val effectClass = NapalmCannonEffect::class
}

fun TowerDefinition.Builder.SpecializationBuilder.napalmCannonEffect() {
    definition = NapalmCannonSpecialization(50f, 25f)
}
