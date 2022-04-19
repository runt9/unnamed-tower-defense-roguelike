package com.runt9.untdrl.view.duringRun.game.building

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.mapToFloats
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class BuildingViewModel(val building: Building) : ViewModel() {
    val position = Binding(building.position.cpy())
    val rotation = Binding(building.rotation)
    val isSelected = Binding(false)
    val attrs = Binding(building.attrs.mapToFloats())
    val isValidPlacement = Binding(false)
    val texture = building.texture
}
