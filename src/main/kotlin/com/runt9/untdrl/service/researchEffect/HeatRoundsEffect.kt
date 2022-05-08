package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.HeatRoundsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class HeatRoundsEffect(
    override val eventBus: EventBus,
    private val definition: HeatRoundsDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rocketTower) return

        tower.addAttributeModifier(AttributeModifier(AttributeType.DAMAGE, percentModifier = definition.damageIncrease))
        tower.damageTypes.filter { it.type == DamageType.PHYSICAL || it.type == DamageType.HEAT }.forEach { it.penetration += definition.penetration }
        towerService.recalculateAttrsSync(tower)
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
