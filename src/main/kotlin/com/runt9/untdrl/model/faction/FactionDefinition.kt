package com.runt9.untdrl.model.faction

import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.model.tower.definition.prototypeTower

interface FactionDefinition {
    val id: Int
    val name: String
    val maxHp: Int
    val startingTower: TowerDefinition

    class Builder {
        internal lateinit var startingTower: TowerDefinition

        fun startingTower(tower: TowerDefinition) {
            startingTower = tower
        }
    }
}

fun faction(
    id: Int,
    name: String,
    maxHp: Int,
    init: FactionDefinition.Builder.() -> Unit
): FactionDefinition {
    val builder = FactionDefinition.Builder()
    builder.init()

    return object : FactionDefinition {
        override val id = id
        override val name = name
        override val maxHp = maxHp
        override val startingTower = builder.startingTower
    }
}

val baseFaction = faction(1, "StarMerCorp", 25) {
    startingTower(prototypeTower)
}
