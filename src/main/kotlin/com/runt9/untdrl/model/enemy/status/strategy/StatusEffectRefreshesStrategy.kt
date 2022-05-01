package com.runt9.untdrl.model.enemy.status.strategy

import com.runt9.untdrl.model.enemy.status.StatusEffect
import com.runt9.untdrl.util.ext.unTdRlLogger

class StatusEffectRefreshesStrategy<S : StatusEffect<S>> : StatusEffectApplyStrategy<S> {
    private val logger = unTdRlLogger()
    override fun apply(existingEffects: MutableCollection<StatusEffect<*>>, newEffect: S) {
        val existingEffect = existingEffects.find { isSameEffect(it, newEffect) }
        if (existingEffect == null) {
            logger.info { "Applying ${newEffect::class.simpleName} to enemy" }
            existingEffects += newEffect
        } else {
            logger.info { "Refreshing ${existingEffect::class.simpleName}" }
            existingEffect.timer.targetTime = newEffect.duration
        }
    }
}

fun <S : StatusEffect<S>> refreshes() = StatusEffectRefreshesStrategy<S>()
