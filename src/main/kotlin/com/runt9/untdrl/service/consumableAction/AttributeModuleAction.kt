package com.runt9.untdrl.service.consumableAction

import com.runt9.untdrl.model.loot.definition.AttributeModuleDefinition
import com.runt9.untdrl.service.duringRun.TowerService

class AttributeModuleAction(
    private val definition: AttributeModuleDefinition,
    private val towerService: TowerService
) : ConsumableAction {
    override fun canApply() = true

    override fun apply() {
        towerService.forEachTower { tower ->
            tower.modifyAllAttributes(percentModifier = definition.attrIncrease, isTemporary = true)
            towerService.recalculateAttrsSync(tower)
        }
    }
}
