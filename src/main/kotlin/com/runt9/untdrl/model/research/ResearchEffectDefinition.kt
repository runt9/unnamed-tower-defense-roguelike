package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.researchEffect.ResearchEffect
import com.runt9.untdrl.service.researchEffect.TowerUnlockEffect
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
abstract class ResearchEffectDefinition(val effectClass: KClass<out ResearchEffect>)

class TowerUnlockEffectDefinition(val towerDef: TowerDefinition) : ResearchEffectDefinition(TowerUnlockEffect::class)
