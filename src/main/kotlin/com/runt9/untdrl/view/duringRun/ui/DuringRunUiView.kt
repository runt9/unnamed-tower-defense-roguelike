package com.runt9.untdrl.view.duringRun.ui

import com.runt9.untdrl.util.framework.ui.view.ScreenView

class DuringRunUiView(override val controller: DuringRunUiController, override val vm: DuringRunUiViewModel) : ScreenView(controller, vm) {
    override fun init() {
        super.init()

        val controller = controller
        val vm = vm
    }
}
