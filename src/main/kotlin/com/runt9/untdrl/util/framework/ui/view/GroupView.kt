package com.runt9.untdrl.util.framework.ui.view

import com.badlogic.gdx.scenes.scene2d.Group
import ktx.scene2d.KGroup
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

abstract class GroupView(override val controller: Controller, override val vm: ViewModel) : Group(), View, KGroup {
    override fun update() = Unit

    override fun remove(): Boolean {
        clear()
        return super.remove()
    }

    override fun dispose() {
        remove()
    }
}
