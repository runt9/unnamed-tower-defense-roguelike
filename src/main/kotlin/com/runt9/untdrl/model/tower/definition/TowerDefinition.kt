package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.UnitTexture

interface TowerDefinition {
    val id: Int
    val name: String
    val texture: UnitTexture
    val projectileTexture: UnitTexture
}
