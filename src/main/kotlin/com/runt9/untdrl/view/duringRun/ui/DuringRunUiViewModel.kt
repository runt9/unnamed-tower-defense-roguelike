package com.runt9.untdrl.view.duringRun.ui

import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class DuringRunUiViewModel : ViewModel() {
    val relics = ListBinding<Relic>()
}
