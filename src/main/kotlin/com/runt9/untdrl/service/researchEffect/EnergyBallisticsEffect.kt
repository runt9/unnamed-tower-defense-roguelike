package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.building.proc.StunProc
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.research.EnergyBallisticsEffectDefinition
import com.runt9.untdrl.service.buildingAction.ProjectileAttackAction
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.util.framework.event.EventBus

class EnergyBallisticsEffect(
    override val eventBus: EventBus,
    private val definition: EnergyBallisticsEffectDefinition,
    private val buildingService: BuildingService
) : ResearchEffect {
    override fun apply() {
        buildingService.forEachBuilding { building ->
            if (building.action !is ProjectileAttackAction) return@forEachBuilding

            building.damageTypes += DamageMap(DamageType.LIGHTNING, definition.lightningDamage)
            building.procs += StunProc(definition.stunChance, definition.stunDuration)
        }
    }
}
