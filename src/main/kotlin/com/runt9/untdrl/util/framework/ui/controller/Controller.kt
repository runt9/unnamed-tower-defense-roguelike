package com.runt9.untdrl.util.framework.ui.controller

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.ui.view.View
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

interface Controller : Disposable {
    val vm: ViewModel
    val view: View

    fun load() = Unit

    override fun dispose() {
        view.dispose()
        vm.dispose()
    }
}
