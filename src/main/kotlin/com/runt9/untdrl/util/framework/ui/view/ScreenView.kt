package com.runt9.untdrl.util.framework.ui.view

import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

abstract class ScreenView(controller: Controller, vm: ViewModel) : TableView(controller, vm) {
    override fun init() {
        setSize(stage.width, stage.height)
    }
}
