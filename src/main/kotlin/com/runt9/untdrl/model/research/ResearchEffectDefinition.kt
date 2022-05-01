package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.researchEffect.AdvancedBallisticsEffect
import com.runt9.untdrl.service.researchEffect.EnergyBallisticsEffect
import com.runt9.untdrl.service.researchEffect.ResearchEffect
import com.runt9.untdrl.service.researchEffect.TowerUnlockEffect
import kotlin.reflect.KClass

interface ResearchEffectDefinition {
    val effectClass: KClass<out ResearchEffect>
}

class TowerUnlockEffectDefinition(val towerDef: TowerDefinition) : ResearchEffectDefinition {
    override val effectClass = TowerUnlockEffect::class
}

fun ResearchDefinition.Builder.unlock(towerDef: TowerDefinition) {
    definition = TowerUnlockEffectDefinition(towerDef)
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
