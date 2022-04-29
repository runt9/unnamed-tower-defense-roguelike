package com.runt9.untdrl.model.tower.action

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.towerAction.InstantAttackAction
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction

class ProjectileAttackActionDefinition(val projectileTexture: UnitTexture, val pierce: Int = 0) : TowerActionDefinition {
    override val actionClass = ProjectileAttackAction::class
}

fun TowerDefinition.Builder.projectileAttack(projectileTexture: UnitTexture, pierce: Int = 0) {
    actionDefinition = ProjectileAttackActionDefinition(projectileTexture, pierce)
}

fun TowerDefinition.Builder.instantAttack() {
    actionDefinition = object : TowerActionDefinition {
        override val actionClass = InstantAttackAction::class
    }
}
