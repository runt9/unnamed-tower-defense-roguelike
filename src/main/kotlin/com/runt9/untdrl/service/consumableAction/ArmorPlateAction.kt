package com.runt9.untdrl.service.consumableAction

import com.runt9.untdrl.model.loot.definition.ArmorPlateDefinition
import com.runt9.untdrl.service.duringRun.RunStateService

class ArmorPlateAction(
    private val definition: ArmorPlateDefinition,
    private val runStateService: RunStateService
) : ConsumableAction {
    override fun canApply() = true

    override fun apply() {
        runStateService.update {
            armor += definition.armor
        }
    }
}
