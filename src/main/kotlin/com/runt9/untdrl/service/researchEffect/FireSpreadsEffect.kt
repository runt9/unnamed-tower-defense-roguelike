package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.enemy.status.Burn
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.tower.definition.NapalmCannonSpecialization
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.view.duringRun.NEARBY

class FireSpreadsEffect(
    override val eventBus: EventBus,
    private val enemyService: EnemyService
) : ResearchEffect {
    override fun apply() {}

    @HandlesEvent
    fun enemyDied(event: EnemyRemovedEvent) {
        if (!event.wasKilled) return

        // NB: Should only ever be one burn, but let's prepare for stacking just in case?
        val burns = event.enemy.statusEffects.filterIsInstance<Burn>().filter {
            it.source.appliedSpecialization?.effect is NapalmCannonSpecialization
        }

        if (burns.isEmpty()) return

        burns.forEach { it.timer.reset(false) }

        enemyService.enemiesInRange(event.enemy.position, NEARBY).forEach { enemy ->
            burns.forEach { enemy.addStatusEffect(it) }
        }
    }
}
