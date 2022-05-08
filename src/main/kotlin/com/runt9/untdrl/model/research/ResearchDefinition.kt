package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.faction.FactionDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.researchEffect.ResearchEffect
import kotlin.reflect.KClass

interface ResearchDefinition {
    val icon: TextureDefinition
    val name: String
    val description: String
    val cost: Int
    val effect: ResearchEffectDefinition
    val dependsOn: List<ResearchDefinition>
    val dependsOnSpecialization: List<KClass<out TowerSpecializationEffectDefinition>>

    class Builder {
        internal val dependsOn = mutableListOf<ResearchDefinition>()
        internal val dependsOnSpecialization = mutableListOf<KClass<out TowerSpecializationEffectDefinition>>()
        internal var description = ""
        lateinit var definition: ResearchEffectDefinition

        fun dependsOn(vararg research: ResearchDefinition) = dependsOn.addAll(research)
        fun dependsOn(vararg specialization: KClass<out TowerSpecializationEffectDefinition>) = dependsOnSpecialization.addAll(specialization)

        operator fun String.unaryPlus() {
            description = this
        }

        operator fun ResearchEffectDefinition.unaryPlus() {
            definition = this
        }

        fun emptyDefinition(effectClass: KClass<out ResearchEffect>) {
            definition = object : ResearchEffectDefinition(effectClass) {}
        }
    }
}

fun FactionDefinition.Builder.research(name: String, icon: TextureDefinition, cost: Int, builder: ResearchDefinition.Builder.() -> Unit = {}): ResearchDefinition {
    val researchBuilder = ResearchDefinition.Builder()
    researchBuilder.builder()

    val research = object : ResearchDefinition {
        override val icon = icon
        override val name = name
        override val description = researchBuilder.description
        override val effect = researchBuilder.definition
        override val cost = cost
        override val dependsOn = researchBuilder.dependsOn
        override val dependsOnSpecialization = researchBuilder.dependsOnSpecialization
    }

    this.research += research

    return research
}

fun FactionDefinition.Builder.unlockTower(towerDef: TowerDefinition, cost: Int) = research(towerDef.name, towerDef.texture, cost) {
    +"Unlocks ${towerDef.name}."
    +TowerUnlockEffectDefinition(towerDef)
}
