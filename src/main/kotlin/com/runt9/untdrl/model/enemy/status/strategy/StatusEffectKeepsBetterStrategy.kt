package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect
import com.runt9.untdrl.util.ext.unTdRlLogger

class StatusEffectKeepsBetterStrategy<S : StatusEffect<S>>(private val comparator: Comparator<S>) : StatusEffectApplyStrategy<S> {
    private val logger = unTdRlLogger()

    @Suppress("UNCHECKED_CAST")
    override fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S) {
        val existing = existingEffects.find { isSameEffect(it, newEffect) }
        if (existing == null) {
            logger.info { "Applying ${newEffect::class.simpleName} to enemy" }
            existingEffects += newEffect
        } else {
            if (comparator.compare(newEffect, existing as S) > 0) {
                existingEffects -= existing
                logger.info { "Replacing existing ${newEffect::class.simpleName} on enemy" }
                existingEffects += newEffect
            }
        }
    }
}

fun <S : StatusEffect<S>> keepsBetter(comparator: Comparator<S>) = StatusEffectKeepsBetterStrategy(comparator)
