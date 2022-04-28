package com.runt9.untdrl.service.factionPassiveEffect

import com.runt9.untdrl.service.duringRun.RunStatePreSaveCallback
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.roundToInt

class RnDBudgetEffect(
    override val eventBus: EventBus,
    private val runStateService: RunStateService
) : FactionPassiveEffect {
    private val logger = unTdRlLogger()
    var incomePct = 0.1f

    private val preSaveCallback: RunStatePreSaveCallback = { old, new ->
        if (new.gold > old.gold) {
            val research = ((new.gold - old.gold) * incomePct).roundToInt()
            logger.info { "Turning ${research}g into research" }
            new.gold -= research
            new.researchAmount += research
        }

        new
    }

    override fun apply() {
        runStateService.beforeSave(preSaveCallback)
    }

    override fun dispose() {
        runStateService.removeBeforeSave(preSaveCallback)
        super.dispose()
    }
}
