package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.UnitTexture

interface ResearchDefinition {
    val icon: UnitTexture
    val name: String
    val description: String
    val cost: Int
    val effect: ResearchEffectDefinition
    val dependsOn: List<ResearchDefinition>
    val exclusiveOf: List<ResearchDefinition>

    fun isExclusiveOf(upgrade: ResearchDefinition) = exclusiveOf.contains(upgrade) || upgrade.exclusiveOf.contains(this)

    class Builder {
        internal val dependsOn = mutableListOf<ResearchDefinition>()
        internal val exclusiveOf = mutableListOf<ResearchDefinition>()
        internal var description = ""
        lateinit var definition: ResearchEffectDefinition

        fun dependsOn(vararg upgrades: ResearchDefinition) = dependsOn.addAll(upgrades)
        fun exclusiveOf(vararg upgrades: ResearchDefinition) = exclusiveOf.addAll(upgrades)
        operator fun String.unaryPlus() {
            description = this
        }
    }
}

fun research(name: String, icon: UnitTexture, cost: Int, builder: ResearchDefinition.Builder.() -> Unit = {}): ResearchDefinition {
    val researchBuilder = ResearchDefinition.Builder()
    researchBuilder.builder()

    return object : ResearchDefinition {
        override val icon = icon
        override val name = name
        override val description = researchBuilder.description
        override val effect = researchBuilder.definition
        override val cost = cost
        override val dependsOn = researchBuilder.dependsOn
        override val exclusiveOf = researchBuilder.exclusiveOf
    }
}
