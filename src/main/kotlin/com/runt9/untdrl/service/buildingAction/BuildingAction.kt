package com.runt9.untdrl.service.buildingAction

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.roundToInt

interface BuildingAction : Disposable {
    val eventBus: EventBus

    suspend fun act(delta: Float)
    fun getStats(): Map<String, String>
    fun levelUp(newLevel: Int)

    fun init() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }

    fun Float.displayInt() = roundToInt().toString()
    fun Float.displayDecimal(decimals: Int = 2) = "%.${decimals}f".format(this)
    fun Float.displayMultiplier() = "${displayDecimal()}x"
    fun Float.displayPercent(decimals: Int = 1) = "${displayDecimal(decimals)}%"
}
