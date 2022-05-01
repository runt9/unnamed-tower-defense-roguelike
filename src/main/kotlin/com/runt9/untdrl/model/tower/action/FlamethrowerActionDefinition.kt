package com.runt9.untdrl.model.tower.action

import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.towerAction.FlamethrowerAction

class FlamethrowerActionDefinition(val angle: Float, val burnChance: Float, val burnDuration: Float, val burnPctOfBase: Float) : TowerActionDefinition {
    override val actionClass = FlamethrowerAction::class
}

fun TowerDefinition.Builder.flamethrower(angle: Float, burnChance: Float, burnDuration: Float, burnPctOfBase: Float) {
    actionDefinition = FlamethrowerActionDefinition(angle, burnChance, burnDuration, burnPctOfBase)
}
