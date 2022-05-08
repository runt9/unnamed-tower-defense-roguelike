package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.faction.VulcanCannonsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.MinigunSpecialization
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.model.tower.intercept.beforeResists
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.specializationEffect.MinigunEffect
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class VulcanCannonsEffect(
    override val eventBus: EventBus,
    private val definition: VulcanCannonsDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rifleTower || !tower.isSpecialization<MinigunSpecialization>()) return

        tower.addInterceptor(beforeResists { _, rr ->
            val stacks = (tower.appliedSpecializationEffect as MinigunEffect).stackCount
            val resistPen = stacks * definition.penPerStack
            rr.addPenetration(DamageType.PHYSICAL, resistPen)
        })
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        if (event.specialization.effect is MinigunSpecialization) {
            applyToTower(event.tower)
        }
    }
}
