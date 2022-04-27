package com.runt9.untdrl.model

import com.runt9.untdrl.model.faction.FactionDefinition
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.Shop
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.research.ResearchDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.ext.randomString
import com.runt9.untdrl.view.duringRun.REROLL_COST
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class RunState(
    val seed: String = Random.randomString(8),
    val faction: FactionDefinition,
    var maxHp: Int = faction.maxHp,
    var hp: Int = faction.maxHp,
    var gold: Int = 100,
    var shopRerollCost: Int = REROLL_COST,
    var researchRerollCost: Int = REROLL_COST,
    var currentShop: Shop = Shop(),
    var availableResearch: List<ResearchDefinition> = listOf(),
    var appliedResearch: List<ResearchDefinition> = listOf(),
    var selectableResearch: List<ResearchDefinition> = listOf(),
    var selectableResearchOptionCount: Int = 5,
    var researchAmount: Int = 0,
    var wave: Int = 1,
    var availableTowers: List<TowerDefinition> = listOf(faction.startingTower),
    var relics: List<Relic> = listOf(),
    var consumableSlots: Int = 3,
    var consumables: List<Consumable> = listOf(),
    var cores: List<TowerCore> = listOf()
)
