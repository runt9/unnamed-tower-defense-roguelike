package com.runt9.untdrl.service.relicEffect

import com.runt9.untdrl.model.loot.definition.MaxHpEffectDefinition
import com.runt9.untdrl.service.duringRun.RunStateService

class MaxHpEffect(
    private val definition: MaxHpEffectDefinition,
    private val runStateService: RunStateService
) : RelicEffect {
    override fun apply() {
        runStateService.update {
            maxHp += definition.maxHp
        }
    }
}
