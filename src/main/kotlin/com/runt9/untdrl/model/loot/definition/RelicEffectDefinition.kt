package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.service.relicEffect.BonusXpPercentEffect
import com.runt9.untdrl.service.relicEffect.MaxHpEffect
import com.runt9.untdrl.service.relicEffect.ReinforcedPlatingEffect
import com.runt9.untdrl.service.relicEffect.RelicEffect
import com.runt9.untdrl.service.relicEffect.SavingsTokenEffect
import com.runt9.untdrl.service.relicEffect.TargetingReticleEffect
import kotlin.reflect.KClass

abstract class RelicEffectDefinition(val effectClass: KClass<out RelicEffect>)

fun emptyDefinition(effectClass: KClass<out RelicEffect>) = object : RelicEffectDefinition(effectClass) {}

class BonusXpPercentEffectDefinition(val xpPercent: Float) : RelicEffectDefinition(BonusXpPercentEffect::class)
class MaxHpEffectDefinition(val maxHp: Int) : RelicEffectDefinition(MaxHpEffect::class)
class ReinforcedPlatingDefinition(val armorPerWave: Int) : RelicEffectDefinition(ReinforcedPlatingEffect::class)
class TargetingReticleDefinition(val damageMultiplier: Float) : RelicEffectDefinition(TargetingReticleEffect::class)
class SavingsTokenDefinition(val goldPerWave: Int) : RelicEffectDefinition(SavingsTokenEffect::class)
