package com.runt9.untdrl.model.research

import com.runt9.untdrl.service.researchEffect.AdvancedBallisticsEffect
import com.runt9.untdrl.service.researchEffect.EnergyBallisticsEffect
import com.runt9.untdrl.service.researchEffect.ResearchEffect
import kotlin.reflect.KClass

interface ResearchEffectDefinition {
    val effectClass: KClass<out ResearchEffect>
}

class AdvancedBallisticsEffectDefinition(val damagePct: Float, val penetration: Float) : ResearchEffectDefinition {
    override val effectClass = AdvancedBallisticsEffect::class
}

fun ResearchDefinition.Builder.advancedBallistics(damagePct: Float, penetration: Float) {
    definition = AdvancedBallisticsEffectDefinition(damagePct, penetration)
}

class EnergyBallisticsEffectDefinition(val lightningDamage: Float, val stunChance: Float, val stunDuration: Float) : ResearchEffectDefinition {
    override val effectClass = EnergyBallisticsEffect::class
}

fun ResearchDefinition.Builder.energyBallistics(lightningDamage: Float, stunChance: Float, stunDuration: Float) {
    definition = EnergyBallisticsEffectDefinition(lightningDamage, stunChance, stunDuration)
}
