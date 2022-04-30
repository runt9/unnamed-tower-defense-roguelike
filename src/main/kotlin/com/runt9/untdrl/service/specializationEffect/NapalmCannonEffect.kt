package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeType.AREA_OF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.NapalmCannonSpecialization
import com.runt9.untdrl.model.tower.proc.BurnProc
import com.runt9.untdrl.util.framework.event.EventBus

class NapalmCannonEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: NapalmCannonSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(DAMAGE, percentModifier = -definition.damageReduction)
        tower.modifyBaseAndLevelGrowth(AREA_OF_EFFECT, percentModifier = definition.aoeGain)

        tower.damageTypes.removeIf { it.type == DamageType.PHYSICAL }
        tower.damageTypes.find { it.type == DamageType.HEAT }?.pctOfBase = 1f
        tower.procs += BurnProc(1f, 2f, 0.5f)
    }
}
