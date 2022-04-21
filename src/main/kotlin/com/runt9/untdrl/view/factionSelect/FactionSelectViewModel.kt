package com.runt9.untdrl.view.factionSelect

import com.runt9.untdrl.model.faction.FactionDefinition
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class FactionSelectViewModel : ViewModel() {
    val seed = Binding("")
    var factionOptions = listOf<FactionDefinition>()
    lateinit var selectedFaction: FactionDefinition
}
