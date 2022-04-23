package com.runt9.untdrl.util.framework.ui.controller

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.ui.view.View
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.MainDispatcher

interface Controller : Disposable {
    val vm: ViewModel
    val view: View

    fun load() = Unit

    override fun dispose() {
        view.dispose()
        vm.dispose()
    }

    fun launchOnRenderingThread(block: suspend CoroutineScope.() -> Unit) = KtxAsync.launch(MainDispatcher, block = block)
}
