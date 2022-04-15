package com.runt9.untdrl.model.building.action

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.service.buildingAction.ProjectileAttackAction

abstract class ProjectileAttackActionDefinition : BuildingActionDefinition {
    override val actionClass = ProjectileAttackAction::class

    abstract val projectileTexture: UnitTexture
    abstract val range: Int
    abstract val attackTime: Float
    abstract val damage: Float
}
