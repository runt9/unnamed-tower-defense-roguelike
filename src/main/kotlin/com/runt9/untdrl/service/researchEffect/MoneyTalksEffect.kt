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
            towerService.forEachTower { t ->
                t.attrMods -= attrMod
                attrMod = AttributeModifier(AttributeType.DAMAGE, percentModifier = pctIncrease)
                t.attrMods += attrMod
                towerService.recalculateAttrs(t)
            }
        }
    }

    @HandlesEvent
    suspend fun towerPlaced(event: TowerPlacedEvent) {
        val tower = event.tower
        tower.attrMods += attrMod
        towerService.recalculateAttrs(tower)
    }

    @HandlesEvent
    fun runStateUpdated(event: RunStateUpdated) {
        recalculate()
    }
}
