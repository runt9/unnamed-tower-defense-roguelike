package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect

class StatusEffectRefreshesStrategy<S : StatusEffect<S>> : StatusEffectApplyStrategy<S> {
    override fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S) {
        val existingEffect = existingEffects.find { isSameEffect(it, newEffect) }
        if (existingEffect == null) {
            existingEffects += newEffect
        } else {
            existingEffect.timer.targetTime = newEffect.duration
        }
    }
}

fun <S : StatusEffect<S>> refreshes() = StatusEffectRefreshesStrategy<S>()
