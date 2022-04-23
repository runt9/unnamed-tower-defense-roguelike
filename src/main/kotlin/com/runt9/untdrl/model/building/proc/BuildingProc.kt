package com.runt9.untdrl.model.building.proc

import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.enemy.Stun

interface BuildingProc {
    val chance: Float

    fun applyToEnemy(enemy: Enemy)
}

data class StunProc(override val chance: Float, val duration: Float) : BuildingProc {
    override fun applyToEnemy(enemy: Enemy) {
        enemy.addStatusEffect(Stun(duration))
    }
}
