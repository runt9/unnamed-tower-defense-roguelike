package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.utils.Disposable

class RunInitializer(private val runServiceRegistry: RunServiceRegistry) : Disposable {
    fun initialize() {
        runServiceRegistry.startAll()
    }

    override fun dispose() {
        runServiceRegistry.stopAll()
    }
}
