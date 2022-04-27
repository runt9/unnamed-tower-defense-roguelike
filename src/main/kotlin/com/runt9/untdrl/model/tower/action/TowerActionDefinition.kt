package com.runt9.untdrl.model.tower.action

import com.runt9.untdrl.service.towerAction.TowerAction
import kotlin.reflect.KClass

interface TowerActionDefinition {
    val actionClass: KClass<out TowerAction>
}
