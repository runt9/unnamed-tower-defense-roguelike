package com.runt9.untdrl.model.tower.definition

import com.runt9.untdrl.model.UnitTexture

object PrototypeTowerDefinition : TowerDefinition {
    override val id = 1
    override val name = "Prototype"
    override val texture = UnitTexture.BOSS
    override val projectileTexture = UnitTexture.HERO
    override val range = 4
    override val attackTime = 2f
    override val damage = 50f
}
