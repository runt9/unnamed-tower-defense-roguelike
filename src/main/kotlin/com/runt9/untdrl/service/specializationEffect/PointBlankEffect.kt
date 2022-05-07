package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.PointBlankSpecialization
import com.runt9.untdrl.service.towerAction.PointBlankSpecializationAction
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.dynamicInjectCheckAssignableFrom
import com.runt9.untdrl.util.framework.event.EventBus

class PointBlankEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: PointBlankSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(RANGE, percentModifier = -definition.rangeReduction)
        tower.modifyBaseAndLevelGrowth(ATTACK_SPEED, percentModifier = -definition.attackSpeedReduction)

        val action = dynamicInject(PointBlankSpecializationAction::class, dynamicInjectCheckAssignableFrom(Tower::class.java) to tower)
        action.init()

        tower.action = action
        tower.canChangeTargetingMode = false
    }
}
