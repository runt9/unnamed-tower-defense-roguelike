package com.runt9.untdrl.model

import com.runt9.untdrl.model.building.definition.PrototypeBuildingDefinition
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.util.ext.randomString
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class RunState(
    val seed: String = Random.randomString(8),
    var hp: Int = 25,
    var gold: Int = 100,
    var research: Int = 0,
    var wave: Int = 1,
    val availableBuildings: MutableList<BuildingDefinition> = mutableListOf(PrototypeBuildingDefinition)
)
