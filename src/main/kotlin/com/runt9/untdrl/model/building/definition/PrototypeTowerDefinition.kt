package com.runt9.untdrl.model.building.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.action.ProjectileAttackActionDefinition

object PrototypeBuildingDefinition : BuildingDefinition {
    override val id = 1
    override val name = "Prototype"
    override val type = BuildingType.TOWER
    override val texture = UnitTexture.BOSS
    override val goldCost = 30

    override val action = object : ProjectileAttackActionDefinition() {
        override val projectileTexture = UnitTexture.HERO
        override val range = 4
        override val attackTime = 1f
        override val damage = 50f
    }
}
