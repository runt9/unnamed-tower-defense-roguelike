package com.runt9.untdrl.view.duringRun.ui.topBar

import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class TopBarViewModel : ViewModel() {
    val gold = Binding(0)
    val wave = Binding(1)
    val isDuringWave = Binding(false)
}
