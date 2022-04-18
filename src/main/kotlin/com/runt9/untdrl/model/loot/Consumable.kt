package com.runt9.untdrl.model.loot

import kotlinx.serialization.Serializable

@Serializable
class Consumable(override val rarity: Rarity) : LootItem {
    override val type = LootItemType.CONSUMABLE
}
