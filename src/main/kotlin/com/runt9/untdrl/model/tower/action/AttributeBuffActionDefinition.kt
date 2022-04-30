package com.runt9.untdrl.model.tower.action

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.service.towerAction.AttributeBuffAction

class AttributeBuffActionDefinition(vararg val modifiers: AttributeModifier) : TowerActionDefinition {
    override val actionClass = AttributeBuffAction::class
}
