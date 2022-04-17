package com.runt9.untdrl.model

import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.building.definition.GoldMineDefinition
import com.runt9.untdrl.model.building.definition.PrototypeTowerDefinition
import com.runt9.untdrl.model.building.definition.ResearchLabDefinition
import com.runt9.untdrl.model.loot.BuildingCore
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.Relic
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
    var availableBuildings: List<BuildingDefinition> = listOf(PrototypeTowerDefinition, GoldMineDefinition, ResearchLabDefinition),
    var relics: List<Relic> = listOf(),
    var consumables: List<Consumable> = listOf(),
    var cores: List<BuildingCore> = listOf()
)
