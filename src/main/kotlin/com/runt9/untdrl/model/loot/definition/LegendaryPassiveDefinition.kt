package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.service.corePassiveEffect.EveryXShotGuaranteedCritPassive
import com.runt9.untdrl.service.corePassiveEffect.LegendaryPassiveEffect
import kotlin.reflect.KClass

interface LegendaryPassiveDefinition {
    val description: String
    val effect: LegendaryPassiveEffectDefinition
}

fun legendaryPassive(description: String, effect: LegendaryPassiveEffectDefinition) = object : LegendaryPassiveDefinition {
    override val description = description
    override val effect = effect
}

abstract class LegendaryPassiveEffectDefinition(val effectClass: KClass<out LegendaryPassiveEffect>)

class EveryXShotGuaranteedCritPassiveDefinition(val shots: Int) : LegendaryPassiveEffectDefinition(EveryXShotGuaranteedCritPassive::class)

val thirdShotCritPassive = legendaryPassive(
    "Every 3rd shot from this tower is a guaranteed crit, with a bonus +50% to Crit Multiplier",
    EveryXShotGuaranteedCritPassiveDefinition(3)
)

val availableLegendaryPassives = listOf(thirdShotCritPassive)
