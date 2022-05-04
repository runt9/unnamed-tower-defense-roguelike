package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.model.loot.Rarity

interface RelicDefinition {
    val name: String
    val rarity: Rarity
    val description: String
    val effect: RelicEffectDefinition
}

fun relic(name: String, rarity: Rarity, description: String, effect: RelicEffectDefinition) = object : RelicDefinition {
    override val name = name
    override val rarity = rarity
    override val description = description
    override val effect = effect
}

val bookOfWonders = relic("Book of Wonders", Rarity.COMMON, "Towers gain 25% increased XP", BonusXpPercentEffectDefinition(0.25f))

val availableRelics = mapOf(
    Rarity.COMMON to listOf(bookOfWonders),
    Rarity.UNCOMMON to listOf(),
    Rarity.RARE to listOf(),
    Rarity.LEGENDARY to listOf(),
)
