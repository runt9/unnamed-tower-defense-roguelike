package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.enemy.status.Bleed
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.ExpandingShrapnelDefinition
import com.runt9.untdrl.model.tower.intercept.onCrit
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ExpandingShrapnelEffect(
    override val eventBus: EventBus,
    private val definition: ExpandingShrapnelDefinition,
    private val towerService: TowerService,
    private val shrapnelEffect: ShrapnelEffect
) : ResearchEffect {
    private val critInterceptor = onCrit { _, onCrit ->
        onCrit.enemy.statusEffects.filterIsInstance<Bleed>().forEach { bleed ->
            bleed.timer.reset(false)
        }
    }

    override fun apply() {
        shrapnelEffect.duration *= definition.durationMod
        towerService.forEachTower { it.addInterceptor(critInterceptor) }
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        event.tower.addInterceptor(critInterceptor)
    }
}
