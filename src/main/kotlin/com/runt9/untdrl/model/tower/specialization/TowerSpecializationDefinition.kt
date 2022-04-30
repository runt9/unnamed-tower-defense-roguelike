package com.runt9.untdrl.model.tower.specialization

import com.runt9.untdrl.model.TextureDefinition

interface TowerSpecializationDefinition {
    val icon: TextureDefinition
    val name: String
    val description: String
    val effect: TowerSpecializationEffectDefinition
    val dependsOn: List<TowerSpecializationDefinition>
}
