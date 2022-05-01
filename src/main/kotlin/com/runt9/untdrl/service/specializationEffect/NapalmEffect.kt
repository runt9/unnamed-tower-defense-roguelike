package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.NapalmSpecialization
import com.runt9.untdrl.model.tower.proc.burnProc
import com.runt9.untdrl.service.towerAction.FlamethrowerAction
import com.runt9.untdrl.util.framework.event.EventBus

class NapalmEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: NapalmSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(DAMAGE, percentModifier = -definition.damageReduction)

        tower.procs.remove((tower.action as FlamethrowerAction).burnProc)
        tower.procs += burnProc(1f, definition.burnDuration, pctOfBaseDamage = definition.burnPctOfBase)
    }
}
