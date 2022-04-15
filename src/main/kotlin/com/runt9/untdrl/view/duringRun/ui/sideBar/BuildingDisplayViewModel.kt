package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class BuildingDisplayViewModel(val empty: Boolean = true) : ViewModel() {
    // TODO: Need a binding to the building so this updates in real-time
    companion object {
        fun fromBuilding(building: Building): BuildingDisplayViewModel {
            val applyInfo: BuildingDisplayViewModel.(Building) -> Unit = { b ->
                name(b.definition.name)
                xp(b.xp)
                xpToLevel(b.xpToLevel)
                level(b.level)
                stats(b.action.getStats())
            }

            val vm = BuildingDisplayViewModel(false)
            vm.applyInfo(building)
            building.onChange { vm.applyInfo(this) }
            return vm
        }
    }

    val name = Binding("")
    val xp = Binding(0)
    val xpToLevel = Binding(0)
    val level = Binding(1)
    val stats = Binding(mapOf<String, String>())
}
