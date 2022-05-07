package com.runt9.untdrl.service.consumableAction

import com.runt9.untdrl.model.loot.definition.AttributeAugmentDefinition
import com.runt9.untdrl.service.duringRun.TowerService

class AttributeAugmentAction(
    private val definition: AttributeAugmentDefinition,
    private val towerService: TowerService
) : ConsumableAction {
    override fun canApply() = true

    override fun apply() {
        towerService.forEachTower { tower ->
            tower.modifyAllAttributes(percentModifier = definition.attrIncrease)
            towerService.recalculateAttrsSync(tower)
        }
    }
}
