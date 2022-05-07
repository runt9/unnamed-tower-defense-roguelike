package com.runt9.untdrl.service.consumableAction

import com.runt9.untdrl.model.loot.definition.HullPartsDefinition
import com.runt9.untdrl.service.duringRun.RunStateService

class HullPartsAction(
    private val definition: HullPartsDefinition,
    private val runStateService: RunStateService
) : ConsumableAction {
    override fun canApply() = true

    override fun apply() {
        runStateService.update {
            maxHp += definition.maxHp
        }
    }
}
