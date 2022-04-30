package com.runt9.untdrl.model.tower

import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.attribute.AttributeType.AREA_OF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.BUFF_DEBUFF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_CHANCE
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_MULTI
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.PROJECTILE_COUNT
import com.runt9.untdrl.model.attribute.AttributeType.RANGE

// If something is requesting an attribute, it better be there, otherwise some definition went wrong along the way, so force not null
fun Tower.attr(type: AttributeType) = attrs[type]!!()
val Tower.range get() = attr(RANGE)
val Tower.damage get() = attr(DAMAGE)
val Tower.attackSpeed get() = attr(ATTACK_SPEED)
val Tower.attackTime get() = 1f / attr(ATTACK_SPEED)
val Tower.critChance get() = attr(CRIT_CHANCE)
val Tower.critMulti get() = attr(CRIT_MULTI)
val Tower.projCount get() = attr(PROJECTILE_COUNT)
val Tower.aoe get() = attr(AREA_OF_EFFECT)
val Tower.buffEffect get() = attr(BUFF_DEBUFF_EFFECT)
