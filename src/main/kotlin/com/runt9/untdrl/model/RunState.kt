package com.runt9.untdrl.model

import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.building.definition.prototypeTowerDefinition
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.Shop
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.research.Research
import com.runt9.untdrl.util.ext.randomString
import com.runt9.untdrl.view.duringRun.REROLL_COST
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class RunState(
    val seed: String = Random.randomString(8),
    var hp: Int = 25,
    var gold: Int = 100,
    var shopRerollCost: Int = REROLL_COST,
    var researchRerollCost: Int = REROLL_COST,
    var currentShop: Shop = Shop(),
    var availableResearch: List<Research> = listOf(),
    var appliedResearch: List<Research> = listOf(),
    var selectableResearch: List<Research> = listOf(),
    var selectableResearchOptionCount: Int = 5,
    var researchAmount: Int = 0,
    var wave: Int = 1,
    var availableBuildings: List<BuildingDefinition> = listOf(prototypeTowerDefinition),
    var relics: List<Relic> = listOf(),
    var consumables: List<Consumable> = listOf(),
    var cores: List<TowerCore> = listOf()
)
