package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.UnitTexture

// TODO: Probably the way I want to handle tower stats is via a TowerStat object.
//  It can handle holding the value as well as the level growth and each tower definition can add in the tower stats relevant to it (composition over inheritance)

interface TowerDefinition {
    val id: Int
    val name: String
    val texture: UnitTexture
    val projectileTexture: UnitTexture
    val range: Int
    val attackTime: Float
    val damage: Float
    val goldCost: Int
}
