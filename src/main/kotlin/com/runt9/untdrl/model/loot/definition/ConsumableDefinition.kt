package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.model.loot.Rarity

interface ConsumableDefinition {
    val name: String
    val rarity: Rarity
    val description: String
    val action: ConsumableActionDefinition
}

fun consumable(name: String, rarity: Rarity, description: String, action: ConsumableActionDefinition) = object : ConsumableDefinition {
    override val name = name
    override val rarity = rarity
    override val description = description
    override val action = action
}

val healthPotion = consumable("Health Potion", Rarity.COMMON, "Restores 25% of Base's Max HP", HealingPotionActionDefinition(0.25f))
val greaterHealthPotion = consumable("Greater Health Potion", Rarity.UNCOMMON, "Restores 50% of Base's Max HP", HealingPotionActionDefinition(0.50f))
val superiorHealthPotion = consumable("Superior Health Potion", Rarity.RARE, "Restores 75% of Base's Max HP", HealingPotionActionDefinition(0.75f))
val perfectHealthPotion = consumable("Perfect Health Potion", Rarity.LEGENDARY, "Fully restores Base's HP to maximum", HealingPotionActionDefinition(1f))

val availableConsumables = mapOf(
    Rarity.COMMON to listOf(healthPotion),
    Rarity.UNCOMMON to listOf(greaterHealthPotion),
    Rarity.RARE to listOf(superiorHealthPotion),
    Rarity.LEGENDARY to listOf(perfectHealthPotion),
)
