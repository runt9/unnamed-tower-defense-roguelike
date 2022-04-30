package com.runt9.untdrl.service.corePassiveEffect

import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.loot.definition.EveryXShotGuaranteedCritPassiveDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.critCheck
import com.runt9.untdrl.model.tower.intercept.onAttack
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class EveryXShotGuaranteedCritPassive(
    override val tower: Tower,
    val definition: EveryXShotGuaranteedCritPassiveDefinition,
    override val eventBus: EventBus
) : LegendaryPassiveEffect {
    private val logger = unTdRlLogger()
    private var counter = 0

    private val attackInterceptor = onAttack { _, _ ->
        logger.info { "On attack, incrementing counter" }
        counter++
    }

    private val critInterceptor = critCheck { _, request ->
        if (counter < definition.shots) return@critCheck
        logger.info { "On crit check, counter is ${definition.shots}, adding guaranteed crit" }
        counter = 0
        request.addCritChance(1f)
        request.addCritMulti(0.5f)
    }

    override fun apply() {
        tower.addInterceptor(attackInterceptor)
        tower.addInterceptor(critInterceptor)
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        counter = 0
    }
}
