package com.runt9.untdrl.model.faction

import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.building.definition.goldMine
import com.runt9.untdrl.model.building.definition.prototypeTower
import com.runt9.untdrl.model.building.definition.researchLab

interface FactionDefinition {
    val id: Int
    val name: String
    val goldBuildings: List<BuildingDefinition>
    val researchBuildings: List<BuildingDefinition>
    val startingTowers: List<BuildingDefinition>

    class Builder {
        internal val goldBuildings = mutableListOf<BuildingDefinition>()
        internal val researchBuildings = mutableListOf<BuildingDefinition>()
        internal val startingTowers = mutableListOf<BuildingDefinition>()

        fun goldBuildings(vararg buildings: BuildingDefinition) {
            goldBuildings += buildings
        }

        fun researchBuildings(vararg buildings: BuildingDefinition) {
            researchBuildings += buildings
        }

        fun startingTowers(vararg buildings: BuildingDefinition) {
            startingTowers += buildings
        }
    }
}

fun faction(
    id: Int,
    name: String,
    init: FactionDefinition.Builder.() -> Unit
): FactionDefinition {
    val builder = FactionDefinition.Builder()
    builder.init()

    return object : FactionDefinition {
        override val id = id
        override val name = name
        override val goldBuildings = builder.goldBuildings
        override val researchBuildings = builder.researchBuildings
        override val startingTowers = builder.startingTowers
    }
}

val baseFaction = faction(1, "Default") {
    goldBuildings(goldMine)
    researchBuildings(researchLab)
    startingTowers(prototypeTower)
}
