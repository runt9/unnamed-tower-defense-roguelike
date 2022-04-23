package com.runt9.untdrl.service.consumableAction

import com.runt9.untdrl.model.loot.definition.HealingPotionActionDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.unTdRlLogger
import kotlin.math.min
import kotlin.math.roundToInt

class HealingPotionAction(
    private val definition: HealingPotionActionDefinition,
    private val runStateService: RunStateService
) : ConsumableAction {
    private val logger = unTdRlLogger()

    override fun canApply() = runStateService.load().run { hp != maxHp }

    override fun apply() {
        logger.info { "Applying consumable" }
        runStateService.update {
            if (hp != maxHp) {
                val hpToHeal = (definition.healingPercent * maxHp).roundToInt()
                hp = min(hp + hpToHeal, maxHp)
                logger.info { "Ready to save runState after applying consumable" }
            }
        }
    }
}
