package com.runt9.untdrl.util.framework.ui.core

import com.badlogic.gdx.InputMultiplexer
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.ui.DialogManager
import com.runt9.untdrl.util.framework.ui.controller.Controller

abstract class UiScreen : UnTdRlScreen {
    val uiStage = UnTdRlStage()
    val input by lazyInject<InputMultiplexer>()
    val dialogManager by lazyInject<DialogManager>()
    override val stages = listOf(uiStage)
    abstract val uiController: Controller

    override fun show() {
        input.addProcessor(uiStage)
        uiController.load()
        uiStage.setView(uiController.view)
        dialogManager.currentStage = uiStage
    }

    override fun hide() {
        input.removeProcessor(uiStage)
        uiController.dispose()
        dialogManager.currentStage = null
    }
}
