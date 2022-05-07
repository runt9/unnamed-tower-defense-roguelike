package com.runt9.untdrl.service.relicEffect

import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.loot.definition.SavingsTokenDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.HandlesEvent

class SavingsTokenEffect(
    private val definition: SavingsTokenDefinition,
    private val runStateService: RunStateService
) : RelicEffect {
    var builtTower = false

    override fun apply() {
    }

    @HandlesEvent(TowerPlacedEvent::class)
    fun towerPlaced() {
        builtTower = true
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        if (!builtTower) {
            runStateService.update {
                val goldGained = definition.goldPerWave * wave
                gold += goldGained
            }
        }

        builtTower = false
    }
}
