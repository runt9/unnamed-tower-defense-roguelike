package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.WindCannonSpecialization
import com.runt9.untdrl.model.tower.proc.SlowProc
import com.runt9.untdrl.service.towerAction.PulseCannonAction
import com.runt9.untdrl.util.framework.event.EventBus

class WindCannonEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: WindCannonSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.damageTypes.find { it.type == DamageType.ENERGY }?.apply {
            pctOfBase -= definition.coldConversion
        }
        tower.damageTypes += DamageMap(DamageType.COLD, definition.coldConversion)
        val towerProc = (tower.action as PulseCannonAction).resistLowerProc
        tower.procs.remove(towerProc)

        tower.addProc(SlowProc(1f, definition.slowDuration, definition.slowPct))
    }
}
