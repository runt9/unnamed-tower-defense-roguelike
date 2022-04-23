package com.runt9.untdrl.service.researchEffect

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.event.EventBus

interface ResearchEffect : Disposable {
    val eventBus: EventBus

    fun init() {
        eventBus.registerHandlers(this)
    }

    fun apply()

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }
}
