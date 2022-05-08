package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.HeRoundsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class HeRoundsEffect(
    override val eventBus: EventBus,
    private val definition: HeRoundsDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rocketTower) return

        tower.attrMods += AttributeModifier(AttributeType.DAMAGE, percentModifier = definition.attrIncrease)
        tower.attrMods += AttributeModifier(AttributeType.AREA_OF_EFFECT, percentModifier = definition.attrIncrease)
        towerService.recalculateAttrsSync(tower)
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
