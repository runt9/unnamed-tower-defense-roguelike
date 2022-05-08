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
import com.runt9.untdrl.service.towerAction.PropagandaTowerAction
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class RiseToTheOccasionEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: RiseToTheOccasionDefinition,
    private val tickerRegistry: TickerRegistry,
    private val towerService: TowerService
) : TowerSpecializationEffect {
    private var stacks = 0
    var retainStacksPct = 0f

    override fun apply() {
        tickerRegistry.registerTimer(1f, action = ::addStack)
        towerService.removeAttributes(tower, BUFF_DEBUFF_EFFECT)
        stacks = 0
        applyModifiers()
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun removeAllStacks() {
        stacks = (stacks.toFloat() * retainStacksPct).roundToInt()
        applyModifiers()
    }

    private fun addStack() {
        stacks++
        applyModifiers()
    }

    private fun applyModifiers() {
        (tower.action as PropagandaTowerAction).apply {
            attrModification.baseModifiers = getModifiers()
            recalculateModifiers()
        }
    }

    private fun getModifiers() = if (stacks == 0) emptySet() else setOf(
        AttributeModifier(DAMAGE, percentModifier = stacks * definition.stackPerSecond),
        AttributeModifier(ATTACK_SPEED, percentModifier = stacks * definition.stackPerSecond)
    )
}
