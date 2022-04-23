package com.runt9.untdrl.service.upgradeEffect

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.util.framework.event.EventBus

interface BuildingUpgradeEffect : Disposable {
    val eventBus: EventBus
    val building: Building

    fun init() {
        eventBus.registerHandlers(this)
    }

    fun apply()

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }
}
