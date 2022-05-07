package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.ToxicFumesSpecialization
import com.runt9.untdrl.model.tower.proc.poisonProc
import com.runt9.untdrl.util.framework.event.EventBus

class ToxicFumesEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: ToxicFumesSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(DAMAGE, percentModifier = -definition.damageReduction)

        tower.addProc(poisonProc(definition.poisonChance, definition.poisonDuration, pctOfBaseDamage = definition.poisonPctOfBase))
        tower.damageTypes.find { it.type == DamageType.HEAT }?.apply { pctOfBase -= definition.natureDamageConversion }
        tower.damageTypes += DamageMap(DamageType.NATURE, definition.natureDamageConversion)
    }
}
