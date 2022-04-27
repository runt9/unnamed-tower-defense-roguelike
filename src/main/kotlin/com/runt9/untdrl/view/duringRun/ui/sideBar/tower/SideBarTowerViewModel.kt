package com.runt9.untdrl.view.duringRun.ui.sideBar.tower

import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.tower.TargetingMode
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationDefinition
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarTowerViewModel(val empty: Boolean = true) : ViewModel() {
    val coreInventoryShown = Binding(false)
    val coreInventory = ListBinding<TowerCore>()
    val id = Binding(0)
    val name = Binding("")
    val xp = Binding(0)
    val xpToLevel = Binding(0)
    val level = Binding(1)
    val attrs = Binding(mapOf<AttributeType, Float>())
    val maxCores = Binding(1)
    val cores = ListBinding<TowerCore>()
    val specializationPoints = Binding(0)
    val availableSpecializations = ListBinding<TowerSpecializationDefinition>()
    val targetingMode = Binding(TargetingMode.FRONT)
}
