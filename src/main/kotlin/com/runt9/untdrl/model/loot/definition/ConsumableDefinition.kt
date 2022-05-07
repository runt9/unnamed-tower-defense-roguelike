package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.model.loot.Rarity
import com.runt9.untdrl.util.ext.displayPercent

interface ConsumableDefinition {
    val name: String
    val rarity: Rarity
    val description: String
    val action: ConsumableActionDefinition
}

val availableConsumables = mapOf<Rarity, MutableList<ConsumableDefinition>>(
    Rarity.COMMON to mutableListOf(),
    Rarity.UNCOMMON to mutableListOf(),
    Rarity.RARE to mutableListOf(),
    Rarity.LEGENDARY to mutableListOf(),
)

fun consumable(name: String, rarity: Rarity, description: String, action: ConsumableActionDefinition) {
    val consumable = object : ConsumableDefinition {
        override val name = name
        override val rarity = rarity
        override val description = description
        override val action = action
    }

    availableConsumables[rarity]!!.add(consumable)
}

fun <D : Number> multiTierConsumable(
    baseName: String,
    descriptionFormat: String,
    commonVal: D,
    uncommonVal: D,
    rareVal: D,
    legendaryVal: D,
    numDisplay: D.() -> String = { toString() },
    constructor: (D) -> ConsumableActionDefinition
) {
    consumable(baseName, Rarity.COMMON, descriptionFormat.format(commonVal.numDisplay()), constructor(commonVal))
    consumable("Greater $baseName", Rarity.UNCOMMON, descriptionFormat.format(uncommonVal.numDisplay()), constructor(uncommonVal))
    consumable("Superior $baseName", Rarity.RARE, descriptionFormat.format(rareVal.numDisplay()), constructor(rareVal))
    consumable("Perfect $baseName", Rarity.LEGENDARY, descriptionFormat.format(legendaryVal.numDisplay()), constructor(legendaryVal))
}

fun initConsumables() {
    multiTierConsumable("Repair Kit", "Restores %s of Ship's Max HP", 0.25f, 0.5f, 0.75f, 1f, { displayPercent(0) }) { RepairKitDefinition(it) }
    multiTierConsumable("Book", "Adds %s XP to all towers", 20, 50, 100, 100) { BookDefinition(it) }
    multiTierConsumable("Armor Plate", "Ship gains %s armor", 1, 5, 10, 20) { ArmorPlateDefinition(it) }
    multiTierConsumable("Hull Parts", "Ship gains %s Max HP", 2, 4, 6, 10) { HullPartsDefinition(it) }
    multiTierConsumable("Attribute Module", "Towers gain %s increased attributes this wave", 5f, 10f, 20f, 40f, { (this / 100f).displayPercent(0) }) { AttributeModuleDefinition(it) }
    multiTierConsumable("Attribute Augment", "Towers permanently gain %s increased attributes", 1f, 2f, 5f, 10f, { (this / 100f).displayPercent(0) }) { AttributeAugmentDefinition(it) }
    multiTierConsumable("Piggy Bank", "Immediately gain %s Gold", 50, 100, 500, 1000) { PiggyBankDefinition(it) }
}
