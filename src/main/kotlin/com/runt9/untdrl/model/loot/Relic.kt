package com.runt9.untdrl.model.loot

import com.runt9.untdrl.model.loot.definition.RelicDefinition
import com.runt9.untdrl.service.relicEffect.RelicEffect
import kotlinx.serialization.Serializable

@Serializable
class Relic(override val rarity: Rarity, val definition: RelicDefinition) : LootItem {
    override val name = definition.name
    override val type = LootItemType.RELIC
    override val description = definition.description

    lateinit var effect: RelicEffect
}
