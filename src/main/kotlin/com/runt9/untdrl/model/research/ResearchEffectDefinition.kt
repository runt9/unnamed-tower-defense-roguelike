package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.tower.definition.TowerDefinition
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
