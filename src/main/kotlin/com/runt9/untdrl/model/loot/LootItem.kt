package com.runt9.untdrl.model.loot

import com.badlogic.gdx.graphics.Color

sealed interface LootItem {
    val type: LootItemType
    val rarity: Rarity
}

enum class LootItemType(val color: Color, val baseCost: Int) {
    RELIC(Color.GREEN, 300),
    CONSUMABLE(Color.BLUE, 100),
    CORE(Color.RED, 200);
}
