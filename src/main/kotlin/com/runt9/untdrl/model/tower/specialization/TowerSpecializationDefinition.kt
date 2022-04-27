package com.runt9.untdrl.model.tower.specialization

import com.runt9.untdrl.model.UnitTexture

interface TowerSpecializationDefinition {
    val icon: UnitTexture
    val name: String
    val description: String
    val effect: TowerSpecializationEffectDefinition
    val dependsOn: List<TowerSpecializationDefinition>
}
