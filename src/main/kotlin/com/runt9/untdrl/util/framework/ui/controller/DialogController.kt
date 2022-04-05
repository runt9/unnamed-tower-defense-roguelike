package com.runt9.untdrl.util.framework.ui.controller

import com.runt9.untdrl.util.framework.ui.core.UnTdRlStage
import com.runt9.untdrl.util.framework.ui.view.DialogView

abstract class DialogController : Controller {
    abstract override val view: DialogView
    private var stage: UnTdRlStage? = null

    var isShown = false

    fun show(stage: UnTdRlStage) {
        if (!isShown) {
            load()
            this.stage = stage
            view.show(stage)
            view.init()
            isShown = true
        }
    }

    fun hide() {
        if (isShown) {
            view.hide()
            isShown = false
            stage = null
            dispose()
        }
    }
}
