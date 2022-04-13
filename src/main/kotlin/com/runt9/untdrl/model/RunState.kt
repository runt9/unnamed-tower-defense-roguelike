package com.runt9.untdrl.model

import com.runt9.untdrl.model.tower.definition.PrototypeTowerDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.ext.randomString
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class RunState(
    val seed: String = Random.randomString(8),
    var hp: Int = 25,
    var gold: Int = 0,
    var research: Int = 0,
    var wave: Int = 1,
    val availableTowers: MutableList<TowerDefinition> = mutableListOf(PrototypeTowerDefinition)
)
