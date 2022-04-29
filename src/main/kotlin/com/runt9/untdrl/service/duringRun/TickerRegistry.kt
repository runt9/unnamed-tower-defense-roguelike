package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.util.framework.event.EventBus

class TickerRegistry(eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val tickers = mutableListOf<(Float) -> Unit>()

    fun registerTicker(ticker: (Float) -> Unit) {
        tickers += ticker
    }

    fun unregisterTicker(ticker: (Float) -> Unit) {
        tickers -= ticker
    }

    override fun tick(delta: Float) {
        tickers.toList().forEach { it(delta) }
    }
}
