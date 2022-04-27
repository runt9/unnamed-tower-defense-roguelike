package com.runt9.untdrl.service.relicEffect

import com.runt9.untdrl.model.loot.definition.BonusXpPercentEffectDefinition
import com.runt9.untdrl.service.duringRun.TowerService

class BonusXpPercentEffect(
    private val definition: BonusXpPercentEffectDefinition,
    private val towerService: TowerService
) : RelicEffect {
    override fun apply() {
        towerService.addGlobalXpModifier(definition.xpPercent)
    }
}
