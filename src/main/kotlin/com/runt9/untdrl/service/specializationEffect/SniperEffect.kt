package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.specialization.SniperDefinition
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus

class SniperEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: SniperDefinition
) : TowerSpecializationEffect {
    override fun apply() {
        tower.attrMods += definition.modifiers
        (tower.action as ProjectileAttackAction).pierce = -1
        // No need to recalculate attrs, automatically done after specializations applied since most specializations need it
    }
}
