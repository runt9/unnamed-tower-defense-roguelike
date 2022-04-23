package com.runt9.untdrl.view.duringRun.ui.sideBar.infoPanel

import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarInfoPanelViewModel : ViewModel() {
    val maxHp = Binding(0)
    val hp = Binding(0)
    val gold = Binding(0)
    val research = Binding(0)
    val wave = Binding(1)
}
