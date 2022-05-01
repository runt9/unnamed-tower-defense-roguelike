package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect
import com.runt9.untdrl.util.ext.unTdRlLogger

class StatusEffectStacksStrategy<S : StatusEffect<S>>(private val stackLimit: Int = -1) : StatusEffectApplyStrategy<S> {
    private val logger = unTdRlLogger()

    override fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S) {
        val existingStacks = existingEffects.filter { isSameEffect(it, newEffect) }
        if (stackLimit == -1 || existingStacks.size < stackLimit) {
            logger.info { "Applying stack of ${newEffect::class.simpleName} to enemy" }
            existingEffects += newEffect
        }
    }
}

fun <S : StatusEffect<S>> stacks(limit: Int = -1) = StatusEffectStacksStrategy<S>(limit)
