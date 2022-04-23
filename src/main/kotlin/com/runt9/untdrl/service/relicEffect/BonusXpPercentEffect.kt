package com.runt9.untdrl.service.relicEffect

import com.runt9.untdrl.model.loot.definition.BonusXpPercentEffectDefinition
import com.runt9.untdrl.service.duringRun.BuildingService

class BonusXpPercentEffect(
    private val definition: BonusXpPercentEffectDefinition,
    private val buildingService: BuildingService
) : RelicEffect {
    override fun apply() {
        buildingService.addGlobalXpModifier(definition.xpPercent)
    }
}
