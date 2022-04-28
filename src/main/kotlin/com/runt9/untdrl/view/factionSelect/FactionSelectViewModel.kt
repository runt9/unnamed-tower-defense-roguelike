package com.runt9.untdrl.view.factionSelect

import com.runt9.untdrl.model.faction.FactionDefinition
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class FactionSelectViewModel(options: List<FactionDefinition>) : ViewModel() {
    val seed = Binding("")
    val factionOptions = ListBinding(options)
    val selectedFaction = Binding(options[0])
}
