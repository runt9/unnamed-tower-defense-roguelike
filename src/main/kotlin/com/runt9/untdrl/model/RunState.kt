package com.runt9.untdrl.model

import com.runt9.untdrl.model.faction.FactionDefinition
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.Shop
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.research.ResearchItem
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.ext.randomString
import com.runt9.untdrl.view.duringRun.INITIAL_GOLD_PURSE_MAX
import com.runt9.untdrl.view.duringRun.REROLL_COST
import com.runt9.untdrl.view.duringRun.STARTING_GOLD
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class RunState(
    val seed: String = Random.randomString(8),
    val faction: FactionDefinition,
    var maxHp: Int = faction.maxHp,
    var hp: Int = faction.maxHp,
    var gold: Int = STARTING_GOLD,
    var goldPurseMax: Int = INITIAL_GOLD_PURSE_MAX,
    var shopRerollCost: Int = REROLL_COST,
    var researchRerollCost: Int = REROLL_COST,
    var currentShop: Shop = Shop(),
    var availableResearch: List<ResearchItem> = listOf(),
    var appliedResearch: List<ResearchItem> = listOf(),
    var selectableResearch: List<ResearchItem> = listOf(),
    var selectableResearchOptionCount: Int = 5,
    // TODO:
    var researchAmount: Int = 1000,
    var wave: Int = 1,
    var availableTowers: List<TowerDefinition> = listOf(faction.startingTower),
    var relics: List<Relic> = listOf(),
    var consumableSlots: Int = 3,
    var consumables: List<Consumable> = listOf(),
    var cores: List<TowerCore> = listOf()
)
