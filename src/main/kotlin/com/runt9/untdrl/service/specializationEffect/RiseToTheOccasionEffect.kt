package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.BUFF_DEBUFF_EFFECT
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.RiseToTheOccasionDefinition
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.AttributeBuffAction
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class RiseToTheOccasionEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: RiseToTheOccasionDefinition,
    private val tickerRegistry: TickerRegistry,
    private val towerService: TowerService
) : TowerSpecializationEffect {
    private val timer = Timer(1f)
    private var stacks = 0

    private val ticker: (Float) -> Unit = { delta ->
        timer.tick(delta)
        if (timer.isReady) {
            addStack()
            timer.reset()
        }
    }

    override fun apply() {
        tickerRegistry.registerTicker(ticker)
        towerService.removeAttribute(tower, BUFF_DEBUFF_EFFECT)
        removeAllStacks()
    }

    override fun dispose() {
        tickerRegistry.unregisterTicker(ticker)
        super.dispose()
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun removeAllStacks() {
        stacks = 0
        (tower.action as AttributeBuffAction).apply {
            attrModification.baseModifiers = emptySet()
            recalculateModifiers()
        }
    }

    private fun addStack() {
        stacks++

        (tower.action as AttributeBuffAction).apply {
            attrModification.baseModifiers = setOf(
                AttributeModifier(DAMAGE, percentModifier = stacks * definition.stackPerSecond),
                AttributeModifier(ATTACK_SPEED, percentModifier = stacks * definition.stackPerSecond)
            )

            recalculateModifiers()
        }
    }
}
