package com.runt9.untdrl.model.attribute

import com.runt9.untdrl.model.attribute.definition.AttributeDefinition
import com.runt9.untdrl.model.attribute.definition.amountPerInterval
import com.runt9.untdrl.model.attribute.definition.attackSpeed
import com.runt9.untdrl.model.attribute.definition.costPerInterval
import com.runt9.untdrl.model.attribute.definition.critChance
import com.runt9.untdrl.model.attribute.definition.critMulti
import com.runt9.untdrl.model.attribute.definition.damage
import com.runt9.untdrl.model.attribute.definition.gainInterval
import com.runt9.untdrl.model.attribute.definition.range

enum class AttributeType(val definition: AttributeDefinition) {
    RANGE(range),
    DAMAGE(damage),
    ATTACK_SPEED(attackSpeed),
    CRIT_CHANCE(critChance),
    CRIT_MULTI(critMulti),
    GAIN_INTERVAL(gainInterval),
    AMOUNT_PER_INTERVAL(amountPerInterval),
    COST_PER_INTERVAL(costPerInterval)
}
