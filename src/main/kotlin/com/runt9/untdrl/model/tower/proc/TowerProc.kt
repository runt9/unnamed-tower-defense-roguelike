package com.runt9.untdrl.model.tower.proc

import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.enemy.Stun

interface TowerProc {
    val chance: Float

    fun applyToEnemy(enemy: Enemy)
}

data class StunProc(override val chance: Float, val duration: Float) : TowerProc {
    override fun applyToEnemy(enemy: Enemy) {
        enemy.addStatusEffect(Stun(duration))
    }
}
