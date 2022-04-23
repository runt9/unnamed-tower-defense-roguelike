package com.runt9.untdrl.service.passiveEffect

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.intercept.beforeDamage
import com.runt9.untdrl.model.building.intercept.onAttack
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.loot.definition.EveryXShotGuaranteedCritPassiveDefinition
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

// TODO: Needs eventBus cause we need to reset counter on wave end
class EveryXShotGuaranteedCritPassive(
    override val building: Building,
    val definition: EveryXShotGuaranteedCritPassiveDefinition,
    override val eventBus: EventBus
) : LegendaryPassiveEffect {
    private val logger = unTdRlLogger()
    private var counter = 0

    private val attackInterceptor = onAttack { _, _ ->
        logger.info { "On attack, incrementing counter" }
        counter++
    }

    private val damageInterceptor = beforeDamage { _, request ->
        if (counter < definition.shots) return@beforeDamage
        logger.info { "Before damage, counter is ${definition.shots}, adding guaranteed crit" }
        counter = 0
        request.addCritChance(1f)
        request.addDamageMultiplier(0.25f)
    }

    override fun apply() {
        building.addInterceptor(attackInterceptor)
        building.addInterceptor(damageInterceptor)
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        counter = 0
    }
}
