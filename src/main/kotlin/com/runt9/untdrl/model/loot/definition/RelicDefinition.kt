package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.model.loot.Rarity
import com.runt9.untdrl.model.loot.Rarity.COMMON
import com.runt9.untdrl.model.loot.Rarity.LEGENDARY
import com.runt9.untdrl.model.loot.Rarity.RARE
import com.runt9.untdrl.model.loot.Rarity.UNCOMMON
import com.runt9.untdrl.service.relicEffect.PouchPocketEffect

interface RelicDefinition {
    val name: String
    val rarity: Rarity
    val description: String
    val effect: RelicEffectDefinition
}

val availableRelics = mapOf<Rarity, MutableList<RelicDefinition>>(
    COMMON to mutableListOf(),
    UNCOMMON to mutableListOf(),
    RARE to mutableListOf(),
    LEGENDARY to mutableListOf(),
)

fun relic(name: String, rarity: Rarity, description: String, effect: RelicEffectDefinition): RelicDefinition {
    val relic = object : RelicDefinition {
        override val name = name
        override val rarity = rarity
        override val description = description
        override val effect = effect
    }

    availableRelics[rarity]!!.add(relic)

    return relic
}

fun initRelics() {
    relic("Book of Wonders", COMMON, "Towers gain 25% increased XP", BonusXpPercentEffectDefinition(0.25f))
    relic("Bolstered Hull", COMMON, "Ship gains 5 max HP", MaxHpEffectDefinition(5))
    relic("Reinforced Plating", COMMON, "Ship gains 2 armor at the beginning of each wave", ReinforcedPlatingDefinition(2))
    relic("Targeting Reticle", COMMON, "Enemies take 10% increased damage from all sources", TargetingReticleDefinition(0.1f))
    relic("Savings Token", COMMON, "Gain 10 gold per wave at the end of a wave where a tower was not built", SavingsTokenDefinition(10))
    relic("Pouch Pocket", COMMON, "Adds an additional slot for collecting loot", emptyDefinition(PouchPocketEffect::class))
}
