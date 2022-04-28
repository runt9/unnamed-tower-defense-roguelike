package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.ext.dynamicInject

class RunInitializer(private val runServiceRegistry: RunServiceRegistry, private val runStateService: RunStateService) : Disposable {
    fun initialize() {
        runServiceRegistry.startAll()
        val runState = runStateService.load()
        val faction = runState.faction

        dynamicInject(faction.goldPassive.effect).apply {
            init()
            apply()
        }

        dynamicInject(faction.researchPassive.effect).apply {
            init()
            apply()
        }
    }

    override fun dispose() {
        runServiceRegistry.stopAll()
    }
}
