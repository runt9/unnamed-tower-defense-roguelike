package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus

typealias Ticker = (Float) -> Unit

class TickerRegistry(eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val tickers = mutableListOf<Ticker>()

    fun registerTicker(ticker: Ticker) {
        tickers += ticker
    }

    fun registerTimer(time: Float, rollover: Boolean = true, action: () -> Unit): Ticker {
        val timer = Timer(time)
        val ticker = { delta: Float ->
            timer.tick(delta)
            if (timer.isReady) {
                action()
                timer.reset(rollover)
            }
        }
        tickers += ticker
        return ticker
    }

    fun unregisterTicker(ticker: Ticker) {
        tickers -= ticker
    }

    override fun tick(delta: Float) {
        tickers.toList().forEach { it(delta) }
    }
}

