package com.runt9.untdrl.model.tower.action

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction

class ProjectileAttackActionDefinition(
    val projectileTexture: TextureDefinition,
    val pierce: Int = 0,
    val speed: Float = 10f,
    val homing: Boolean = true,
    val delayedHoming: Float = 0f,
    val anglePerProjectile: Float = 20f
) : TowerActionDefinition {
    override val actionClass = ProjectileAttackAction::class
}

fun TowerDefinition.Builder.projectileAttack(
    projectileTexture: TextureDefinition,
    pierce: Int = 0,
    speed: Float = 10f,
    homing: Boolean = true,
    delayedHoming: Float = 0f,
    anglePerProjectile: Float = 20f
) {
    actionDefinition = ProjectileAttackActionDefinition(projectileTexture, pierce, speed, homing, delayedHoming, anglePerProjectile)
}
