package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect

class StatusEffectApplyIfEmptyStrategy<S : StatusEffect<S>> : StatusEffectApplyStrategy<S> {
    override fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S) {
        if (existingEffects.none { isSameEffect(it, newEffect) }) {
            existingEffects += newEffect
        }
    }
}

fun <S : StatusEffect<S>> applyIfEmpty() = StatusEffectApplyIfEmptyStrategy<S>()
