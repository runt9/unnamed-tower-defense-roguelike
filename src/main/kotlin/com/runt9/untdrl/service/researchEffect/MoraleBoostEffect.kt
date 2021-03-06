package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.faction.MoraleBoostDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.onKill
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class MoraleBoostEffect(
    override val eventBus: EventBus,
    private val definition: MoraleBoostDefinition,
    private val towerService: TowerService,
    private val tickerRegistry: TickerRegistry
) : ResearchEffect {
    private val trackers = mutableMapOf<Tower, MoraleBoostTracker>()
    private val interceptor = onKill { tower, _ ->
        trackers[tower]?.addStack()
    }

    override fun apply() {
        towerService.forEachTower(::applyToTower)

        tickerRegistry.registerTicker { delta ->
            trackers.forEach { it.value.tick(delta) }
        }
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }

    private fun applyToTower(tower: Tower) {
        trackers[tower] = MoraleBoostTracker(tower)
        tower.addInterceptor(interceptor)
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        trackers.forEach { it.value.reset() }
    }

    inner class MoraleBoostTracker(val tower: Tower) {
        val timer = Timer(definition.duration)
        private val stacks = mutableListOf<AttributeModifier>()

        fun tick(delta: Float) {
            timer.tick(delta)
            if (timer.isReady && stacks.isNotEmpty()) {
                val mod = stacks.removeFirst()
                tower.removeAttributeModifier(mod)
                towerService.recalculateAttrsSync(tower)
                timer.reset(false)
            }
        }

        fun addStack() {
            if (stacks.size >= definition.maxStacks) return

            val stack = AttributeModifier(AttributeType.ATTACK_SPEED, percentModifier = definition.attackSpeedIncrease, isTemporary = true)
            tower.addAttributeModifier(stack)
            stacks += stack
            towerService.recalculateAttrsSync(tower)
            timer.reset(false)
        }

        fun reset() {
            stacks.clear()
            timer.reset(false)
        }
    }
}
