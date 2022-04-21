package com.runt9.untdrl.model.building.upgrade

import com.runt9.untdrl.model.UnitTexture

interface BuildingUpgrade {
    val icon: UnitTexture
    val name: String
    val description: String
    val dependsOn: List<BuildingUpgrade>
    val exclusiveOf: List<BuildingUpgrade>

    fun isExclusiveOf(upgrade: BuildingUpgrade) = exclusiveOf.contains(upgrade) || upgrade.exclusiveOf.contains(this)
}
