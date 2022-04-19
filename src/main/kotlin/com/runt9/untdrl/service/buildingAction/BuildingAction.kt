package com.runt9.untdrl.service.buildingAction

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.event.EventBus

interface BuildingAction : Disposable {
    val eventBus: EventBus

    suspend fun act(delta: Float)

    fun init() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }
}
