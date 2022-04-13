package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class TowerDisplayViewModel(val empty: Boolean = true) : ViewModel() {
    // TODO: Need a binding to the tower so this updates in real-time
    companion object {
        fun fromTower(tower: Tower): TowerDisplayViewModel {
            return TowerDisplayViewModel(false).apply {
                name(tower.definition.name)
                damage(tower.damage)
                range(tower.range)
                attackSpeed(tower.attackTime)
                xp(tower.xp)
                xpToLevel(tower.xpToLevel)
                level(tower.level)
            }
        }
    }

    val name = Binding("")
    val damage = Binding(0f)
    val range = Binding(0)
    val attackSpeed = Binding(0f)
    val xp = Binding(0)
    val xpToLevel = Binding(0)
    val level = Binding(1)
}
