package com.runt9.untdrl.model.research

import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.roundToInt

@Serializable
class ResearchItem(val definition: ResearchDefinition, var discount: Float = 0f) {
    val cost get() = max(0f, definition.cost * (1 - discount)).roundToInt()

    val icon = definition.icon
    val name = definition.name
    val description = definition.description
    val effect = definition.effect
    val dependsOn = definition.dependsOn
}
