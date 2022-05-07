package com.runt9.untdrl.service.consumableAction

import com.runt9.untdrl.model.loot.definition.PiggyBankDefinition
import com.runt9.untdrl.service.duringRun.RunStateService

class PiggyBankAction(
    private val definition: PiggyBankDefinition,
    private val runStateService: RunStateService
) : ConsumableAction {
    override fun canApply() = true

    override fun apply() {
        runStateService.update {
            gold += definition.goldAmt
        }
    }
}
