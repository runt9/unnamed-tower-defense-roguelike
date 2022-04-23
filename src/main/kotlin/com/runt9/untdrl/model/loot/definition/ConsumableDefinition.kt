package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.model.loot.Rarity

interface ConsumableDefinition {
    val name: String
    val rarity: Rarity
    val description: String
    val action: ConsumableActionDefinition
}

val healthPotion = object : ConsumableDefinition {
    override val name = "Health Potion"
    override val rarity = Rarity.COMMON
    override val description = "Restores 25% of Base's Max HP"
    override val action = HealingPotionActionDefinition(0.25f)
}

val greaterHealthPotion = object : ConsumableDefinition {
    override val name = "Greater Health Potion"
    override val rarity = Rarity.UNCOMMON
    override val description = "Restores 50% of Base's Max HP"
    override val action = HealingPotionActionDefinition(0.50f)
}

val superiorHealthPotion = object : ConsumableDefinition {
    override val name = "Superior Health Potion"
    override val rarity = Rarity.RARE
    override val description = "Restores 75% of Base's Max HP"
    override val action = HealingPotionActionDefinition(0.75f)
}

val perfectHealthPotion = object : ConsumableDefinition {
    override val name = "Perfect Health Potion"
    override val rarity = Rarity.LEGENDARY
    override val description = "Fully restores Base's HP to maximum"
    override val action = HealingPotionActionDefinition(1f)
}

val availableConsumables = mapOf(
    Rarity.COMMON to listOf(healthPotion),
    Rarity.UNCOMMON to listOf(greaterHealthPotion),
    Rarity.RARE to listOf(superiorHealthPotion),
    Rarity.LEGENDARY to listOf(perfectHealthPotion),
)
