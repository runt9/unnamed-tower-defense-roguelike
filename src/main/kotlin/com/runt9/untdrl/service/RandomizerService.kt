package com.runt9.untdrl.service

import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.service.duringRun.RunService
import com.runt9.untdrl.service.duringRun.RunServiceRegistry
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.random
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.random.Random

class RandomizerService(private val runStateService: RunStateService, eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    lateinit var rng: Random

    override fun startInternal() {
        val state = runStateService.load()
        rng = Random(state.seed.hashCode())
    }

    fun <T> randomize(action: (Random) -> T) = action(rng)

    fun <T : Comparable<T>> randomize(lucky: Boolean = false, action: (Random) -> T): T {
        val first = action(rng)
        if (lucky) {
            val second = action(rng)
            return maxOf(first, second)
        }

        return first
    }

    fun percentChance(percentChance: Float) = rng.nextFloat() <= percentChance
    fun coinFlip() = rng.nextBoolean()

    fun randomAttributeModifier(luckyValue: Boolean, type: AttributeType): AttributeModifier {
        val range = type.definition.rangeForRandomizer
        val rangeType = range.type

        val value = randomize(luckyValue) { range(range.range, luckyValue) }

        return AttributeModifier(
            type,
            flatModifier = if (rangeType == AttributeModificationType.FLAT) value else 0f,
            percentModifier = if (rangeType == AttributeModificationType.PERCENT) value else 0f
        )
    }

    fun range(range: ClosedFloatingPointRange<Float>, lucky: Boolean = false) = randomize(lucky) {
        range.random(it)
    }
}
