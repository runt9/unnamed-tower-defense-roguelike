package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect

class StatusEffectKeepsBetterStrategy<S : StatusEffect<S>>(private val comparator: Comparator<S>) : StatusEffectApplyStrategy<S> {
    @Suppress("UNCHECKED_CAST")
    override fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S) {
        val existing = existingEffects.find { isSameEffect(it, newEffect) }
        if (existing == null) {
            existingEffects += newEffect
        } else {
            if (comparator.compare(newEffect, existing as S) > 0) {
                existingEffects -= existing
                existingEffects += newEffect
            }
        }
    }
}

fun <S : StatusEffect<S>> keepsBetter(comparator: Comparator<S>) = StatusEffectKeepsBetterStrategy(comparator)
