package com.runt9.untdrl.service.relicEffect

import com.runt9.untdrl.service.duringRun.RunStateService

class ToolbeltEffect(private val runStateService: RunStateService) : RelicEffect {
    override fun apply() {
        runStateService.update {
            consumableSlots += 2
        }
    }
}
