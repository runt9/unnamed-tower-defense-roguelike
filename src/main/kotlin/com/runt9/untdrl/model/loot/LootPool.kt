package com.runt9.untdrl.model.loot

class LootPool {
    var gold = 0
    var items = mutableListOf<LootItem>()

    fun clear() {
        gold = 0
        items.clear()
    }
}
