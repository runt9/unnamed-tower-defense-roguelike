package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.service.corePassiveEffect.EveryXShotGuaranteedCritPassive
import com.runt9.untdrl.service.corePassiveEffect.LegendaryPassiveEffect
import kotlin.reflect.KClass

interface LegendaryPassiveDefinition {
    val description: String
    val effect: LegendaryPassiveEffectDefinition
}

interface LegendaryPassiveEffectDefinition {
    val effectClass: KClass<out LegendaryPassiveEffect>
}

class EveryXShotGuaranteedCritPassiveDefinition(val shots: Int) : LegendaryPassiveEffectDefinition {
    override val effectClass = EveryXShotGuaranteedCritPassive::class
}

val thirdShotCritPassive = object : LegendaryPassiveDefinition {
    override val description = "Every 3rd shot from this tower is a guaranteed crit, with a bonus +50% to Crit Multiplier"
    override val effect = EveryXShotGuaranteedCritPassiveDefinition(3)
}

val availableLegendaryPassives = listOf(thirdShotCritPassive)
