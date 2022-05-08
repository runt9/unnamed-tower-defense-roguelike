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

        burns.forEach { it.reset() }

        enemyService.enemiesInRange(event.enemy.position, NEARBY).forEach { enemy ->
            // TODO: This is wrong, adding the same instance of the same status effect to multiple enemies will cause it to tick multiple times
            //  per tick and do less damage and go away sooner
            burns.forEach { enemy.addStatusEffect(it) }
        }
    }
}
