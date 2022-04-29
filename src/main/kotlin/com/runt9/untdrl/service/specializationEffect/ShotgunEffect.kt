package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.Attribute
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.attribute.AttributeType.PROJECTILE_COUNT
import com.runt9.untdrl.model.attribute.AttributeType.RANGE
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.specialization.ShotgunSpecialization
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus

class ShotgunEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: ShotgunSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.attrMods += AttributeModifier(DAMAGE, percentModifier = -definition.attributeReduction)
        tower.attrMods += AttributeModifier(RANGE, percentModifier = -definition.attributeReduction)
        tower.attrs[PROJECTILE_COUNT] = Attribute(PROJECTILE_COUNT, 5f)
        tower.attrBase[PROJECTILE_COUNT] = 5f
        (tower.action as ProjectileAttackAction).apply {
            homing = false
        }
    }
}
