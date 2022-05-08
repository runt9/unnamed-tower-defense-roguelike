package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.MoneyTalksDefinition
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class MoneyTalksEffect(
    override val eventBus: EventBus,
    private val definition: MoneyTalksDefinition,
    private val runStateService: RunStateService,
    private val towerService: TowerService
) : ResearchEffect {
    private var attrMod = AttributeModifier(AttributeType.DAMAGE, percentModifier = 0f)

    override fun apply() {
        recalculate()
    }

    private fun recalculate() {
        val pctIncrease = (runStateService.load().gold.toFloat() / definition.goldPerDmg).roundToInt().toFloat()

        if (pctIncrease != attrMod.percentModifier) {
            val newAttrMod = AttributeModifier(AttributeType.DAMAGE, percentModifier = pctIncrease)

            towerService.forEachTower { t ->
                // TODO: Don't mess with modifiers for towers without damage attr
                t.removeAttributeModifier(attrMod)
                t.addAttributeModifier(newAttrMod)
                towerService.recalculateAttrsSync(t)
            }

            attrMod = newAttrMod
        }
    }

    @HandlesEvent
    suspend fun towerPlaced(event: TowerPlacedEvent) {
        val tower = event.tower
        tower.addAttributeModifier(attrMod)
        towerService.recalculateAttrs(tower)
    }

    @HandlesEvent
    fun runStateUpdated(event: RunStateUpdated) {
        recalculate()
    }
}
