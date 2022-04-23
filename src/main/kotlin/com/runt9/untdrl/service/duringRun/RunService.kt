package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.event.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

@Suppress("LeakingThis")
abstract class RunService(private val eventBus: EventBus, registry: RunServiceRegistry) : Disposable {
    val serviceContext = newSingleThreadAsyncContext("Service-Thread")

    init {
        registry.register(this)
    }

    fun start() {
        eventBus.registerHandlers(this)
        startInternal()
    }

    fun stop() {
        eventBus.unregisterHandlers(this)
        stopInternal()
    }

    fun launchOnServiceThread(block: suspend CoroutineScope.() -> Unit) = KtxAsync.launch(serviceContext, block = block)
    suspend fun onServiceThread(block: suspend CoroutineScope.() -> Unit) = withContext(serviceContext, block = block)

    protected open fun startInternal() = Unit
    protected open fun stopInternal() = Unit

    open fun tick(delta: Float) = Unit
    override fun dispose() = Unit
}
