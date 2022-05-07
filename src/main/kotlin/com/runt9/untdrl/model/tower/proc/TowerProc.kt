package com.runt9.untdrl.model.tower.proc

import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.enemy.status.Burn
import com.runt9.untdrl.model.enemy.status.DamagingStatusEffect
import com.runt9.untdrl.model.enemy.status.Poison
import com.runt9.untdrl.model.enemy.status.Slow
import com.runt9.untdrl.model.enemy.status.Stun
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.damage

interface TowerProc {
    val chance: Float

    fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float)
}

data class StunProc(override val chance: Float, val duration: Float) : TowerProc {
    override fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float) {
        enemy.addStatusEffect(Stun(tower, duration))
    }
}

data class SlowProc(override val chance: Float, val duration: Float, val slowPct: Float) : TowerProc {
    override fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float) {
        enemy.addStatusEffect(Slow(tower, duration, slowPct))
    }
}

data class DotProc<S : DamagingStatusEffect<S>>(
    override val chance: Float,
    val duration: Float,
    val pctOfHitDamage: Float = 0f,
    val pctOfBaseDamage: Float = 0f,
    val constructor: (Tower, Float, Float) -> S
) : TowerProc {
    override fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float) {
        val totalDamage = if (pctOfHitDamage > 0f) finalDamage * pctOfHitDamage else tower.damage * pctOfBaseDamage
        enemy.addStatusEffect(constructor(tower, duration, totalDamage))
    }
}

fun burnProc(chance: Float, duration: Float, pctOfHitDamage: Float = 0f, pctOfBaseDamage: Float = 0f) =
    DotProc(chance, duration, pctOfHitDamage, pctOfBaseDamage) { t, d, td -> Burn(t, d, td) }

fun poisonProc(chance: Float, duration: Float, pctOfHitDamage: Float = 0f, pctOfBaseDamage: Float = 0f) =
    DotProc(chance, duration, pctOfHitDamage, pctOfBaseDamage) { t, d, td -> Poison(t, d, td) }
