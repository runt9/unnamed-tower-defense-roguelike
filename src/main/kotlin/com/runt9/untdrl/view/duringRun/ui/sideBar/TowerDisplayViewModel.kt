package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class TowerDisplayViewModel(val empty: Boolean = true) : ViewModel() {
    companion object {
        fun fromTower(tower: Tower): TowerDisplayViewModel {
            return TowerDisplayViewModel(false).apply {
                name(tower.definition.name)
                damage(tower.damage)
                range(tower.range)
                attackSpeed(tower.attackTimer.targetTime)
            }
        }
    }

    val name = Binding("")
    val damage = Binding(0f)
    val range = Binding(0)
    val attackSpeed = Binding(0f)
}
