package com.runt9.untdrl.view.duringRun.ui.sideBar.building

import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.TargetingMode
import com.runt9.untdrl.model.building.upgrade.BuildingUpgrade
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarBuildingViewModel(val empty: Boolean = true) : ViewModel() {
    val coreInventoryShown = Binding(false)
    val coreInventory = ListBinding<TowerCore>()
    val id = Binding(0)
    val name = Binding("")
    val type = Binding(BuildingType.TOWER)
    val xp = Binding(0)
    val xpToLevel = Binding(0)
    val level = Binding(1)
    val attrs = Binding(mapOf<AttributeType, Float>())
    val maxCores = Binding(1)
    val cores = ListBinding<TowerCore>()
    val upgradePoints = Binding(0)
    val availableUpgrades = ListBinding<BuildingUpgrade>()
    val targetingMode = Binding(TargetingMode.FRONT)
}
