package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture

// TODO: Probably the way I want to handle building stats is via a BuildingStat object.
//  It can handle holding the value as well as the level growth and each building definition can add in the building stats relevant to it (composition over inheritance)

interface BuildingDefinition {
    val id: Int
    val name: String
    val texture: UnitTexture
    val projectileTexture: UnitTexture
    val range: Int
    val attackTime: Float
    val damage: Float
    val goldCost: Int
}