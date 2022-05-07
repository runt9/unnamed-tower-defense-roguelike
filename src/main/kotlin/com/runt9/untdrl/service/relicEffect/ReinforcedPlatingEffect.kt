package com.runt9.untdrl.service.relicEffect

import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.model.loot.definition.ReinforcedPlatingDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ReinforcedPlatingEffect(
    private val definition: ReinforcedPlatingDefinition,
    private val runStateService: RunStateService
) : RelicEffect {
    override fun apply() {}

    @HandlesEvent(WaveStartedEvent::class)
    fun waveStart() {
        runStateService.update {
            armor += definition.armorPerWave
        }
    }
}
