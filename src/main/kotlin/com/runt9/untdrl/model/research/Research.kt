package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.UnitTexture

interface Research {
    val icon: UnitTexture
    val name: String
    val description: String
    val cost: Int
    val dependsOn: List<Research>
    val exclusiveOf: List<Research>

    fun isExclusiveOf(upgrade: Research) = exclusiveOf.contains(upgrade) || upgrade.exclusiveOf.contains(this)

    class Builder {
        internal val dependsOn = mutableListOf<Research>()
        internal val exclusiveOf = mutableListOf<Research>()
        internal var description = ""

        fun dependsOn(vararg upgrades: Research) = dependsOn.addAll(upgrades)
        fun exclusiveOf(vararg upgrades: Research) = exclusiveOf.addAll(upgrades)
        operator fun String.unaryPlus() {
            description = this
        }
    }
}

fun research(name: String, icon: UnitTexture, cost: Int, builder: Research.Builder.() -> Unit = {}): Research {
    val researchBuilder = Research.Builder()
    researchBuilder.builder()

    return object : Research {
        override val icon = icon
        override val name = name
        override val description = researchBuilder.description
        override val cost = cost
        override val dependsOn = researchBuilder.dependsOn
        override val exclusiveOf = researchBuilder.exclusiveOf
    }
}
