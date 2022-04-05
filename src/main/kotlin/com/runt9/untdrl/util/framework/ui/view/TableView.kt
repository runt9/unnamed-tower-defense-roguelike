package com.runt9.untdrl.util.framework.ui.view

import com.kotcrab.vis.ui.widget.VisTable
import ktx.scene2d.KTable
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

abstract class TableView(override val controller: Controller, override val vm: ViewModel) : VisTable(), View, KTable {
    override fun update() = Unit

    override fun remove(): Boolean {
        clear()
        return super.remove()
    }

    override fun dispose() {
        remove()
    }
}
