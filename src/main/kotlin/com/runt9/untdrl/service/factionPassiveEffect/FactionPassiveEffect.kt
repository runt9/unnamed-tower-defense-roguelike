package com.runt9.untdrl.service.factionPassiveEffect

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.event.EventBus

interface FactionPassiveEffect : Disposable {
    val eventBus: EventBus

    fun init() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }
}
