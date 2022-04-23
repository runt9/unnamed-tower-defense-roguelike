package com.runt9.untdrl.model.building.action

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.service.buildingAction.ProjectileAttackAction

class ProjectileAttackActionDefinition(val projectileTexture: UnitTexture, val pierce: Int = 0) : BuildingActionDefinition {
    override val actionClass = ProjectileAttackAction::class
}

fun BuildingDefinition.Builder.projectileAttack(projectileTexture: UnitTexture, pierce: Int = 0) {
    actionDefinition = ProjectileAttackActionDefinition(projectileTexture, pierce)
}
