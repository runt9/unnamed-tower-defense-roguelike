package com.runt9.untdrl.model.enemy.status

import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.enemy.status.strategy.StatusEffectApplyStrategy
import com.runt9.untdrl.model.enemy.status.strategy.applyIfEmpty
import com.runt9.untdrl.model.enemy.status.strategy.keepsBetter
import com.runt9.untdrl.model.enemy.status.strategy.stacks
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.util.ext.Timer


abstract class StatusEffect<S : StatusEffect<S>>(val source: Tower, val duration: Float, val applyStrategy: StatusEffectApplyStrategy<S>) {
    val timer = Timer(duration)

    open fun tick(delta: Float) {
        timer.tick(delta)
    }
}

abstract class DamagingStatusEffect<S : StatusEffect<S>>(
    source: Tower,
    duration: Float,
    val totalDamage: Float,
    val damageType: DamageType,
    val damageSource: DamageSource,
    applyStrategy: StatusEffectApplyStrategy<S>
) : StatusEffect<S>(source, duration, applyStrategy) {
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

class Stun(source: Tower, duration: Float) : StatusEffect<Stun>(source, duration, applyIfEmpty())

class Burn(source: Tower, duration: Float, damage: Float) : DamagingStatusEffect<Burn>(source, duration, damage, DamageType.HEAT, DamageSource.BURN, keepsBetter())

class Poison(source: Tower, duration: Float, damage: Float) : DamagingStatusEffect<Poison>(source, duration, damage, DamageType.NATURE, DamageSource.POISON, stacks())

class Bleed(source: Tower, duration: Float, damage: Float) : DamagingStatusEffect<Bleed>(source, duration, damage, DamageType.PHYSICAL, DamageSource.BLEED, stacks())

class Slow(source: Tower, duration: Float, val slowPct: Float) : StatusEffect<Slow>(source, duration, keepsBetter(Comparator.comparing(Slow::slowPct)))
