package com.runt9.untdrl.model.tower.action

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.towerAction.MineThrowerAction

class MineThrowerActionDefinition(val mineTexture: TextureDefinition) : TowerActionDefinition {
    override val actionClass = MineThrowerAction::class
}

fun TowerDefinition.Builder.mineThrower(mineTexture: TextureDefinition) {
    actionDefinition = MineThrowerActionDefinition(mineTexture)
}
