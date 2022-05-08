package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.PointOfImpactDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.model.tower.intercept.beforeDamage
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class PointOfImpactEffect(
    override val eventBus: EventBus,
    private val definition: PointOfImpactDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rocketTower) return

        tower.addInterceptor(beforeDamage { _, dr ->
            val distanceModifier = 1 - (dr.distanceFromImpact / tower.range)
            val multiplier = definition.damageIncrease * distanceModifier
            dr.addDamageMultiplier(multiplier)
        })
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
