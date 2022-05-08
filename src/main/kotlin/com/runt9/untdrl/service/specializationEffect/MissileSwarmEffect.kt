package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.Attribute
import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeType.AREA_OF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.PROJECTILE_COUNT
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.MissileSwarmSpecialization
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus

class MissileSwarmEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: MissileSwarmSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(DAMAGE, percentModifier = -definition.attributeReduction)
        tower.modifyBaseAndLevelGrowth(AREA_OF_EFFECT, percentModifier = -definition.attributeReduction)

        tower.attrs[PROJECTILE_COUNT] = Attribute(PROJECTILE_COUNT, 3f)
        tower.attrBase[PROJECTILE_COUNT] = 3f
        tower.attrGrowth[PROJECTILE_COUNT] = Pair(FLAT, 0f)

        (tower.action as ProjectileAttackAction).apply {
            delayedHoming = 0.5f
            homing = false
        }
    }
}
