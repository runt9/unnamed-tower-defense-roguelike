package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.utils.Disposable

class RunServiceRegistry : Disposable {
    private val registry = mutableSetOf<RunService>()

    fun register(service: RunService) {
        registry += service
    }

    fun startAll() = registry.forEach(RunService::start)
    fun stopAll() = registry.forEach(RunService::stop)
    fun tickAll(delta: Float) = registry.forEach { s -> s.tick(delta) }
    override fun dispose() = registry.forEach(Disposable::dispose)
}
