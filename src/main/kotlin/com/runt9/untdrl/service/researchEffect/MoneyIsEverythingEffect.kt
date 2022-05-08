package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.faction.MoneyIsEverythingDefinition
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class MoneyIsEverythingEffect(
    override val eventBus: EventBus,
    private val definition: MoneyIsEverythingDefinition,
    private val stockMarket: StockMarketEffect,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {}

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun prepareWave() {
        if (stockMarket.finalProfitMultiplier <= 1.00f) return

        val newAttrMod = AttributeModifier(AttributeType.BUFF_DEBUFF_EFFECT, flatModifier = definition.buffEffectAmt)
        towerService.allTowers.filter { it.definition == propagandaTower }.forEach { tower ->
            tower.addAttributeModifier(newAttrMod)
            towerService.recalculateAttrsSync(tower)
        }
    }
}
