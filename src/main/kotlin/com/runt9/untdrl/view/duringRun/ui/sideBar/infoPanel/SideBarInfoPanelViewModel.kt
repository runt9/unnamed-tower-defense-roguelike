package com.runt9.untdrl.view.duringRun.ui.sideBar.infoPanel

import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SideBarInfoPanelViewModel : ViewModel() {
    val hp = Binding(25)
    val gold = Binding(0)
    val research = Binding(0)
    val wave = Binding(1)
}
