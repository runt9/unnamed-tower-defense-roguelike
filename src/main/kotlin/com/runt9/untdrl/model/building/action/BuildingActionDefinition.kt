package com.runt9.untdrl.model.building.action

import com.runt9.untdrl.service.buildingAction.BuildingAction
import kotlin.reflect.KClass

interface BuildingActionDefinition {
    val actionClass: KClass<out BuildingAction>
}
