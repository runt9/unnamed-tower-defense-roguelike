package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.faction.UndyingFervorDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.RiseToTheOccasionDefinition
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.specializationEffect.RiseToTheOccasionEffect
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class UndyingFervorEffect(
    override val eventBus: EventBus,
    private val definition: UndyingFervorDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        applyToTower(event.tower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != propagandaTower || !tower.isSpecialization<RiseToTheOccasionDefinition>()) return

        (tower.appliedSpecializationEffect as RiseToTheOccasionEffect).retainStacksPct = definition.remainingBonus
    }
}
