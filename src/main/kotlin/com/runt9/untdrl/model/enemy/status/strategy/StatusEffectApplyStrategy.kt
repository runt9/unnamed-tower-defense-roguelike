package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect

interface StatusEffectApplyStrategy<S : StatusEffect<S>> {
    fun isSameEffect(e1: StatusEffect<*>, e2: S) = e1::class == e2::class

    fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S)
}
