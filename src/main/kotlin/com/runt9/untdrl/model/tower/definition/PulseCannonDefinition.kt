package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.TextureDefinition.PULSE_CANNON_PROJECTILE
import com.runt9.untdrl.model.TextureDefinition.RESEARCH_LAB
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.BUFF_DEBUFF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.action.TowerActionDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.PointBlankEffect
import com.runt9.untdrl.service.specializationEffect.RattledBonesEffect
import com.runt9.untdrl.service.specializationEffect.WindCannonEffect
import com.runt9.untdrl.service.towerAction.PulseCannonAction

val pulseCannon = tower("Pulse Cannon", RESEARCH_LAB, 25/*0*/) {
    +"Fires a sound wave towards an enemy that pierces all enemies hit, deals Energy damage, and reduces enemy resistances by 5%."

    +PulseCannonActionDefinition(PULSE_CANNON_PROJECTILE, 0.05f)

    RANGE(4f, 0.1f, FLAT)
    ATTACK_SPEED(0.75f, 0.0375f, FLAT)
    DAMAGE(80f, 15f, FLAT)
    BUFF_DEBUFF_EFFECT(0f, 0.01f, FLAT)

    damage(DamageType.ENERGY)

    specialization("Point Blank", TextureDefinition.PROTOTYPE_TOWER) {
        +"Tower no longer fires a single sound wave, instead a sound wave pulses outward from the tower hitting all nearby enemies. Range reduced by 60% and Attack Speed reduced by 25%"
        +PointBlankSpecialization(60f, 25f)
    }

    specialization("Rattled Bones", TextureDefinition.ENEMY) {
        +"Tower gains 100% increased Buff/Debuff effect and 25% increased Attack Speed, but has 75% reduced Damage."
        +RattledBonesSpecialization(100f, 25f, 75f)
    }

    specialization("Wind Cannon", RESEARCH_LAB) {
        +"Tower now fires a freezing gust of wind at enemies. Converts 50% of base damage to Cold. No longer reduces enemy resistances on hit, instead slows enemies hit by 35% for 2s"
        +WindCannonSpecialization(0.5f, 0.35f, 2f)
    }
}

class PulseCannonActionDefinition(val texture: TextureDefinition, val resistanceReduction: Float) : TowerActionDefinition(PulseCannonAction::class)

class PointBlankSpecialization(val rangeReduction: Float, val attackSpeedReduction: Float) : TowerSpecializationEffectDefinition(PointBlankEffect::class)
class RattledBonesSpecialization(val buffEffectIncrease: Float, val attackSpeedIncrease: Float, val damageReduction: Float) : TowerSpecializationEffectDefinition(RattledBonesEffect::class)
class WindCannonSpecialization(val coldConversion: Float, val slowPct: Float, val slowDuration: Float) : TowerSpecializationEffectDefinition(WindCannonEffect::class)
