package com.runt9.untdrl.model.building.upgrade

import com.runt9.untdrl.model.UnitTexture

interface BuildingUpgradeDefinition {
    val icon: UnitTexture
    val name: String
    val description: String
    val effect: BuildingUpgradeEffectDefinition
    val dependsOn: List<BuildingUpgradeDefinition>
    val exclusiveOf: List<BuildingUpgradeDefinition>

    fun isExclusiveOf(upgrade: BuildingUpgradeDefinition) = exclusiveOf.contains(upgrade) || upgrade.exclusiveOf.contains(this)
}
