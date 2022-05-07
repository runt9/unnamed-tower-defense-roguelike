package com.runt9.untdrl.model.loot.definition

import com.runt9.untdrl.service.consumableAction.ArmorPlateAction
import com.runt9.untdrl.service.consumableAction.AttributeAugmentAction
import com.runt9.untdrl.service.consumableAction.AttributeModuleAction
import com.runt9.untdrl.service.consumableAction.BookAction
import com.runt9.untdrl.service.consumableAction.ConsumableAction
import com.runt9.untdrl.service.consumableAction.HullPartsAction
import com.runt9.untdrl.service.consumableAction.PiggyBankAction
import com.runt9.untdrl.service.consumableAction.RepairKitAction
import kotlin.reflect.KClass

abstract class ConsumableActionDefinition(val actionClass: KClass<out ConsumableAction>)

class RepairKitDefinition(val healingPercent: Float) : ConsumableActionDefinition(RepairKitAction::class)
class BookDefinition(val xpAmt: Int) : ConsumableActionDefinition(BookAction::class)
class ArmorPlateDefinition(val armor: Int) : ConsumableActionDefinition(ArmorPlateAction::class)
class HullPartsDefinition(val maxHp: Int) : ConsumableActionDefinition(HullPartsAction::class)
class AttributeModuleDefinition(val attrIncrease: Float) : ConsumableActionDefinition(AttributeModuleAction::class)
class AttributeAugmentDefinition(val attrIncrease: Float) : ConsumableActionDefinition(AttributeAugmentAction::class)
class PiggyBankDefinition(val goldAmt: Int) : ConsumableActionDefinition(PiggyBankAction::class)
