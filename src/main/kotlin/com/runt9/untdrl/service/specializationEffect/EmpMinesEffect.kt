package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeType.AREA_OF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.EmpMinesSpecialization
import com.runt9.untdrl.util.framework.event.EventBus

class EmpMinesEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: EmpMinesSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(ATTACK_SPEED, percentModifier = -definition.attackSpeedReduction)
        tower.modifyBaseAndLevelGrowth(AREA_OF_EFFECT, percentModifier = definition.aoeGain)

        tower.damageTypes.removeIf { it.type == DamageType.PHYSICAL || it.type == DamageType.HEAT }
        tower.damageTypes += DamageMap(DamageType.ENERGY, 1f, definition.energyPenetration)
    }
}
