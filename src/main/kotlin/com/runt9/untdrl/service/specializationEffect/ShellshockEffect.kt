package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeType.CRIT_CHANCE
import com.runt9.untdrl.model.attribute.AttributeType.CRIT_MULTI
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.ShellshockSpecialization
import com.runt9.untdrl.model.tower.proc.StunProc
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus

class ShellshockEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: ShellshockSpecialization,
    private val towerService: TowerService
) : TowerSpecializationEffect {
    override fun apply() {
        tower.modifyBaseAndLevelGrowth(DAMAGE, percentModifier = -definition.damageReduction)
        towerService.removeAttributes(tower, CRIT_CHANCE, CRIT_MULTI)
        tower.procs += StunProc(definition.stunChance, definition.stunDuration)
    }
}
