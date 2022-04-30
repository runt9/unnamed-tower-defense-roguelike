package com.runt9.untdrl.model.tower.proc

import com.runt9.untdrl.model.enemy.status.Burn
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.enemy.status.Stun
import com.runt9.untdrl.model.tower.Tower

interface TowerProc {
    val chance: Float

    fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float)
}

data class StunProc(override val chance: Float, val duration: Float) : TowerProc {
    override fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float) {
        enemy.addStatusEffect(Stun(tower, duration))
    }
}

data class BurnProc(override val chance: Float, val duration: Float, val pctOfHitDamage: Float) : TowerProc {
    override fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float) {
        enemy.addStatusEffect(Burn(tower, duration, finalDamage * pctOfHitDamage))
    }
}
