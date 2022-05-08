package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.faction.MlrsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.MissileSwarmSpecialization
import com.runt9.untdrl.model.tower.definition.ShotgunSpecialization
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class MlrsEffect(
    override val eventBus: EventBus,
    private val definition: MlrsDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rocketTower || !tower.isSpecialization<MissileSwarmSpecialization>()) return

        tower.attrMods += AttributeModifier(AttributeType.PROJECTILE_COUNT, percentModifier = definition.projIncrease)
        (tower.action as ProjectileAttackAction).homing = false
        towerService.recalculateAttrsSync(tower)
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        if (event.specialization.effect is ShotgunSpecialization) {
            applyToTower(event.tower)
        }
    }
}
