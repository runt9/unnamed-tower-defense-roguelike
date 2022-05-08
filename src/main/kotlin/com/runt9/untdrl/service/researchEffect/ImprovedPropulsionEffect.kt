package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.ImprovedPropulsionDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.flamethrower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.FlamethrowerAction
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ImprovedPropulsionEffect(
    override val eventBus: EventBus,
    private val definition: ImprovedPropulsionDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != flamethrower) return

        tower.addAttributeModifier(AttributeModifier(AttributeType.RANGE, percentModifier = definition.rangeIncrease))
        (tower.action as FlamethrowerAction).angle *= (1 - definition.angleDecrease)
        towerService.recalculateAttrsSync(tower)
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
