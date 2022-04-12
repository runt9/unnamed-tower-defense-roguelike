package com.runt9.untdrl.service

import com.runt9.untdrl.service.duringRun.RunService
import com.runt9.untdrl.service.duringRun.RunServiceRegistry
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.random.Random

class RandomizerService(private val runStateService: RunStateService, eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    lateinit var rng: Random

    override fun startInternal() {
        val state = runStateService.load()
        rng = Random(state.seed.hashCode())
    }

    fun <T> randomize(action: (Random) -> T) = action(rng)

    fun percentChance(percentChance: Float) = rng.nextFloat() <= percentChance
    fun coinFlip() = rng.nextBoolean()
}
