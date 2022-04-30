package com.runt9.untdrl.util.framework.ui.view

import com.kotcrab.vis.ui.widget.VisTable
import ktx.scene2d.KTable

abstract class TableView : VisTable(), View, KTable {
    override fun update() = Unit

    override fun remove(): Boolean {
        clear()
        return super.remove()
    }

    override fun dispose() {
        remove()
    }
}
