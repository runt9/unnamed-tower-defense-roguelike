package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.faction.SpottersDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.model.tower.intercept.onAttack
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class SpottersEffect(
    override val eventBus: EventBus,
    private val definition: SpottersDefinition,
    private val towerService: TowerService,
    private val tickerRegistry: TickerRegistry
) : ResearchEffect {
    private val trackers = mutableMapOf<Tower, SpottersTracker>()

    override fun apply() {
        towerService.forEachTower(::applyToTower)

        tickerRegistry.registerTicker { delta ->
            trackers.forEach { it.value.tick(delta) }
        }
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rocketTower) return

        trackers[tower] = SpottersTracker(tower)
        tower.addInterceptor(onAttack { _, _ ->
            trackers[tower]?.removeStacks()
        })
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        trackers.forEach { it.value.reset() }
    }

    inner class SpottersTracker(val tower: Tower) {
        val timer = Timer(1f)
        private val stacks = mutableListOf<AttributeModifier>()

        fun tick(delta: Float) {
            timer.tick(delta)
            if (timer.isReady) {
                addStack()
                timer.reset(false)
            }
        }

        private fun addStack() {
            val stack = AttributeModifier(AttributeType.RANGE, percentModifier = definition.rangeIncrease, isTemporary = true)
            tower.addAttributeModifier(stack)
            stacks += stack
            towerService.recalculateAttrsSync(tower)
        }

        fun removeStacks() {
            tower.removeAttributeModifiers(stacks)
            towerService.recalculateAttrsSync(tower)
            reset()
        }

        fun reset() {
            stacks.clear()
            timer.reset(false)
        }
    }
}
