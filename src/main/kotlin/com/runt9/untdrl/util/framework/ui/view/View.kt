package com.runt9.untdrl.util.framework.ui.view

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.ui.Updatable
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

interface View : Disposable, Updatable {
    val controller: Controller
    val vm: ViewModel

    fun init()
    override fun update() = Unit
}
