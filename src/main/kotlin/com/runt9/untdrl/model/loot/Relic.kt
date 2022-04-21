package com.runt9.untdrl.model.loot

import kotlinx.serialization.Serializable

@Serializable
class Relic(override val rarity: Rarity) : LootItem {
    override val type = LootItemType.RELIC
    override val description = "Relic"
}
