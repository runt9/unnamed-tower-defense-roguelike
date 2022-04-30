package com.runt9.untdrl.model.enemy

import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.util.ext.Timer

abstract class StatusEffect(val source: Tower, val duration: Float, val stacks: Boolean, val refreshes: Boolean) {
    val timer = Timer(duration)

    open fun tick(delta: Float) {
        timer.tick(delta)
    }
}

abstract class DamagingStatusEffect(
    source: Tower,
    duration: Float,
    val totalDamage: Float,
    val damageType: DamageType,
    val damageSource: DamageSource,
    stacks: Boolean
) : StatusEffect(source, duration, stacks, false) {
    var remainingDamage = totalDamage
    var damageThisTick = 0f
    private val damagePerSecond = totalDamage / duration

    override fun tick(delta: Float) {
        super.tick(delta)

        // RemainingDamage is a way to handle odd rounding errors so we don't accidentally do more damage than we're supposed to
        if (remainingDamage <= 0) {
            damageThisTick = 0f
            return
        }

        damageThisTick = damagePerSecond * delta
        remainingDamage -= damageThisTick
    }
}

class Stun(source: Tower, duration: Float) : StatusEffect(source, duration, false, false)

class Burn(source: Tower, duration: Float, damage: Float) : DamagingStatusEffect(source, duration, damage, DamageType.HEAT, DamageSource.BURN, false)
