package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.service.relicEffect.BonusXpPercentEffect
import com.runt9.untdrl.service.relicEffect.RelicEffect
import kotlin.reflect.KClass

abstract class RelicEffectDefinition(val effectClass: KClass<out RelicEffect>)

class BonusXpPercentEffectDefinition(val xpPercent: Float) : RelicEffectDefinition(BonusXpPercentEffect::class)
