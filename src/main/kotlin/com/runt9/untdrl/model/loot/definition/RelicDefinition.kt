package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.model.loot.Rarity

interface RelicDefinition {
    val name: String
    val rarity: Rarity
    val description: String
    val effect: RelicEffectDefinition
}

val bookOfWonders = object : RelicDefinition {
    override val name = "Book of Wonders"
    override val rarity = Rarity.COMMON
    override val description = "Buildings gain 25% increased XP"
    override val effect = BonusXpPercentEffectDefinition(0.25f)
}

val availableRelics = mapOf(
    Rarity.COMMON to listOf(bookOfWonders),
    Rarity.UNCOMMON to listOf(),
    Rarity.RARE to listOf(),
    Rarity.LEGENDARY to listOf(),
)
