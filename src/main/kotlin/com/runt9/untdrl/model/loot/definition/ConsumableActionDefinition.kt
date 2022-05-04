package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.service.consumableAction.ConsumableAction
import com.runt9.untdrl.service.consumableAction.HealingPotionAction
import kotlin.reflect.KClass

abstract class ConsumableActionDefinition(val actionClass: KClass<out ConsumableAction>)

class HealingPotionActionDefinition(val healingPercent: Float) : ConsumableActionDefinition(HealingPotionAction::class)

