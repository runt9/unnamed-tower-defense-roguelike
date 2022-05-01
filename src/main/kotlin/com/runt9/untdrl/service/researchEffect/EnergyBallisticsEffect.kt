package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.faction.EnergyBallisticsEffectDefinition
import com.runt9.untdrl.model.tower.proc.StunProc
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus

class EnergyBallisticsEffect(
    override val eventBus: EventBus,
    private val definition: EnergyBallisticsEffectDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower { tower ->
            if (tower.action !is ProjectileAttackAction) return@forEachTower

            tower.damageTypes += DamageMap(DamageType.ENERGY, definition.lightningDamage)
            tower.procs += StunProc(definition.stunChance, definition.stunDuration)
        }
    }
}
