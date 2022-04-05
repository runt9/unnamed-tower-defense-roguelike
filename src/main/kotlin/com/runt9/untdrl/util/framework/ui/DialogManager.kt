package com.runt9.untdrl.util.framework.ui

import com.badlogic.gdx.utils.Disposable
import ktx.async.onRenderingThread
import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.event.ShowDialogRequest
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.core.UnTdRlStage

class DialogManager(private val eventBus: EventBus) : Disposable {
    var currentStage: UnTdRlStage? = null

    init {
        eventBus.registerHandlers(this)
    }

    @HandlesEvent
    suspend fun showDialog(event: ShowDialogRequest<*>) = onRenderingThread {
        currentStage?.run {
            val dialog = Injector.getProvider(event.dialogClass.java)()
            dialog.show(this)
        }
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }
}
