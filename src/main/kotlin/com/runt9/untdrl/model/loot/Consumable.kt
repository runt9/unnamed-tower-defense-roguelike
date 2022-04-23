package com.runt9.untdrl.model.loot

import com.runt9.untdrl.model.loot.definition.ConsumableDefinition
import com.runt9.untdrl.service.consumableAction.ConsumableAction
import kotlinx.serialization.Serializable

@Serializable
class Consumable(override val rarity: Rarity, val definition: ConsumableDefinition) : LootItem {
    override val type = LootItemType.CONSUMABLE
    override val name = definition.name
    override val description = definition.description

    lateinit var action: ConsumableAction
}
