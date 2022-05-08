package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.faction.RailgunsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.SniperSpecialization
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class RailgunsEffect(
    override val eventBus: EventBus,
    private val definition: RailgunsDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rifleTower || !tower.isSpecialization<SniperSpecialization>()) return

        tower.attrMods += AttributeModifier(AttributeType.DAMAGE, percentModifier = definition.damageIncrease)
        (tower.action as ProjectileAttackAction).pierce = Int.MAX_VALUE
        towerService.recalculateAttrsSync(tower)
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        if (event.specialization.effect is SniperSpecialization) {
            applyToTower(event.tower)
        }
    }
}
