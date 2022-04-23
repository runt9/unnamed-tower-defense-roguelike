package com.runt9.untdrl.model.building

import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.attribute.AttributeType.AMOUNT_PER_INTERVAL
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.COST_PER_INTERVAL
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_CHANCE
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_MULTI
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.GAIN_INTERVAL
import com.runt9.untdrl.model.attribute.AttributeType.RANGE

// If something is requesting an attribute, it better be there, otherwise some definition went wrong along the way, so force not null
fun Building.attr(type: AttributeType) = attrs[type]!!()
val Building.range get() = attr(RANGE)
val Building.damage get() = attr(DAMAGE)
val Building.attackSpeed get() = attr(ATTACK_SPEED)
val Building.attackTime get() = 1f / attr(ATTACK_SPEED)
val Building.critChance get() = attr(CRIT_CHANCE)
val Building.critMulti get() = attr(CRIT_MULTI)
val Building.gainInterval get() = attr(GAIN_INTERVAL)
val Building.amountPerInterval get() = attr(AMOUNT_PER_INTERVAL)
val Building.costPerInterval get() = attr(COST_PER_INTERVAL)
