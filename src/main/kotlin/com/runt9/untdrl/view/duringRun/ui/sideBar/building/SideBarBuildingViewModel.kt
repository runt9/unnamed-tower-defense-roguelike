package com.runt9.untdrl.view.duringRun.ui.sideBar.building

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.loot.BuildingCore
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarBuildingViewModel(val empty: Boolean = true) : ViewModel() {
    companion object {
        fun fromBuilding(building: Building): SideBarBuildingViewModel {
            val applyInfo: SideBarBuildingViewModel.(Building) -> Unit = { b ->
                name(b.definition.name)
                xp(b.xp)
                xpToLevel(b.xpToLevel)
                level(b.level)
                stats(b.action.getStats())
                maxCores(b.maxCores)
                cores(b.cores)
            }

            val vm = SideBarBuildingViewModel(false)
            vm.applyInfo(building)
            building.onChange { vm.applyInfo(this) }
            return vm
        }
    }

    val coreInventoryShown = Binding(false)
    val coreInventory = ListBinding<BuildingCore>()
    val name = Binding("")
    val xp = Binding(0)
    val xpToLevel = Binding(0)
    val level = Binding(1)
    val stats = Binding(mapOf<String, String>())
    val maxCores = Binding(1)
    val cores = ListBinding<BuildingCore>()
}
