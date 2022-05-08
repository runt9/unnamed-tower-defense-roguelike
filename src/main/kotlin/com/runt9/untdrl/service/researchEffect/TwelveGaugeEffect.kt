package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.faction.TwelveGaugeDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.ShotgunSpecialization
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class TwelveGaugeEffect(
    override val eventBus: EventBus,
    private val definition: TwelveGaugeDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rifleTower || !tower.isSpecialization<ShotgunSpecialization>()) return

        tower.addAttributeModifier(AttributeModifier(AttributeType.DAMAGE, percentModifier = definition.damageIncrease))
        tower.addAttributeModifier(AttributeModifier(AttributeType.PROJECTILE_COUNT, flatModifier = definition.bonusProj.toFloat()))
        (tower.action as ProjectileAttackAction).totalArc * (1 - definition.arcReduction)
        towerService.recalculateAttrsSync(tower)
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        applyToTower(event.tower)
    }
}
