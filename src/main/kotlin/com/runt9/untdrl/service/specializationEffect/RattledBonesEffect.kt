package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.BUFF_DEBUFF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.RattledBonesSpecialization
import com.runt9.untdrl.util.framework.event.EventBus

class RattledBonesEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: RattledBonesSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(BUFF_DEBUFF_EFFECT, percentModifier = definition.buffEffectIncrease)
        tower.modifyBaseAndLevelGrowth(ATTACK_SPEED, percentModifier = definition.attackSpeedIncrease)
        tower.modifyBaseAndLevelGrowth(DAMAGE, percentModifier = -definition.damageReduction)
    }
}
