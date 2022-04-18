package com.runt9.untdrl.model.loot

import kotlinx.serialization.Serializable

@Serializable
data class Shop(
    var relics: Map<Relic, Int> = mapOf(),
    var consumables: Map<Consumable, Int> = mapOf(),
    var cores: Map<TowerCore, Int> = mapOf()
)
