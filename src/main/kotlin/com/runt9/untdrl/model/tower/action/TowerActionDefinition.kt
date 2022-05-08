package com.runt9.untdrl.model.tower.action

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.service.towerAction.AttributeBuffAction
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.service.towerAction.TowerAction
import kotlin.reflect.KClass

abstract class TowerActionDefinition(val actionClass: KClass<out TowerAction>)

class AttributeBuffActionDefinition(vararg val modifiers: AttributeModifier) : TowerActionDefinition(AttributeBuffAction::class)

class ProjectileAttackActionDefinition(
    val projectileTexture: TextureDefinition,
    val pierce: Int = 0,
    val speed: Float = 10f,
    val homing: Boolean = true,
    val delayedHoming: Float = 0f,
    val totalArc: Float = 90f
) : TowerActionDefinition(ProjectileAttackAction::class)
