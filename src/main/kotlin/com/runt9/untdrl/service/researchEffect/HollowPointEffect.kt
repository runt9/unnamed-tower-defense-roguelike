package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.HollowPointDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.intercept.beforeDamage
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class HollowPointEffect(
    override val eventBus: EventBus,
    private val definition: HollowPointDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rifleTower) return

        tower.addInterceptor(beforeDamage(DamageSource.BLEED) { _, dr ->
            dr.addDamageMultiplier(definition.bleedDmgMulti)
        })
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
