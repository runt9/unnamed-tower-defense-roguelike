package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.KineticBallisticsEffectDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.proc.StunProc
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class KineticBallisticsEffect(
    override val eventBus: EventBus,
    private val definition: KineticBallisticsEffectDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.action !is ProjectileAttackAction) return

        tower.damageTypes += DamageMap(DamageType.ENERGY, definition.lightningDamage)
        tower.addProc(StunProc(definition.stunChance, definition.stunDuration))
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
