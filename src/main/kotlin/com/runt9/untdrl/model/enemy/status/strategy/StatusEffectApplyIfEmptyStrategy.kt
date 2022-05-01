package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect
import com.runt9.untdrl.util.ext.unTdRlLogger

class StatusEffectApplyIfEmptyStrategy<S : StatusEffect<S>> : StatusEffectApplyStrategy<S> {
    private val logger = unTdRlLogger()
    override fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S) {
        if (existingEffects.none { isSameEffect(it, newEffect) }) {
            logger.info { "Applying ${newEffect::class.simpleName} to enemy" }
            existingEffects += newEffect
        }
    }
}

fun <S : StatusEffect<S>> applyIfEmpty() = StatusEffectApplyIfEmptyStrategy<S>()
