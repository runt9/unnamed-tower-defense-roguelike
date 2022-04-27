package com.runt9.untdrl.service.passiveEffect

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.framework.event.EventBus

interface LegendaryPassiveEffect : Disposable {
    val eventBus: EventBus
    val tower: Tower

    fun init() {
        eventBus.registerHandlers(this)
    }

    fun apply()

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }
}
