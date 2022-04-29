package com.runt9.untdrl.model.attribute

import com.runt9.untdrl.model.attribute.definition.AttributeDefinition
import com.runt9.untdrl.model.attribute.definition.attackSpeed
import com.runt9.untdrl.model.attribute.definition.critChance
import com.runt9.untdrl.model.attribute.definition.critMulti
import com.runt9.untdrl.model.attribute.definition.damage
import com.runt9.untdrl.model.attribute.definition.projCount
import com.runt9.untdrl.model.attribute.definition.range

enum class AttributeType(val definition: AttributeDefinition) {
    RANGE(range),
    DAMAGE(damage),
    ATTACK_SPEED(attackSpeed),
    CRIT_CHANCE(critChance),
    CRIT_MULTI(critMulti),
    PROJECTILE_COUNT(projCount)
}
