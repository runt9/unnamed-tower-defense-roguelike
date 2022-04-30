package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.enemy.status.Slow
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.buffEffect
import com.runt9.untdrl.model.tower.definition.SayItLouderDefinition
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.towerAction.AttributeBuffAction
import com.runt9.untdrl.util.framework.event.EventBus

class SayItLouderEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: SayItLouderDefinition,
    private val tickerRegistry: TickerRegistry,
    private val enemyService: EnemyService
) : TowerSpecializationEffect {
    private val ticker: (Float) -> Unit = { _ ->
        val effect = tower.buffEffect
        enemyService.enemiesInRange(tower.position, tower.range).forEach { enemy ->
            enemy.addStatusEffect(Slow(tower, 0.1f, definition.slowPercentage * (1 + effect)))
        }
    }

    override fun apply() {
        (tower.action as AttributeBuffAction).apply {
            attrModification.baseModifiers = attrModification.baseModifiers.map {
                AttributeModifier(it.type, it.flatModifier, it.percentModifier - definition.buffReduction)
            }.toSet()
            recalculateModifiers()
        }

        tickerRegistry.registerTicker(ticker)
    }

    override fun dispose() {
        tickerRegistry.unregisterTicker(ticker)
        super.dispose()
    }
}
