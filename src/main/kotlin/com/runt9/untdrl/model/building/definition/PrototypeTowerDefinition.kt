package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_CHANCE
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_MULTI
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.projectileAttack

val prototypeTowerDefinition = building("Prototype Tower", BuildingType.TOWER, UnitTexture.PROTOTYPE_TOWER, 30) {
    projectileAttack(UnitTexture.PROJECTILE)

    RANGE(4f, 0.25f, FLAT)
    ATTACK_SPEED(1f, 0.05f, FLAT)
    DAMAGE(50f, 10f, PERCENT)
    CRIT_CHANCE(0.05f, 10f, PERCENT)
    CRIT_MULTI(1.5f, 0.1f, FLAT)
}
