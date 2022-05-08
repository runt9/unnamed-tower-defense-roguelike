package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class HappyShareholdersEffect(
    override val eventBus: EventBus,
    private val stockMarket: StockMarketEffect,
    private val towerService: TowerService
) : ResearchEffect {
    private var attrMod = AttributeModifier(AttributeType.BUFF_DEBUFF_EFFECT, percentModifier = 0f)

    override fun apply() {}

    @HandlesEvent(PrepareNextWaveEvent::class)
    fun prepareWave() {
        val finalProfit = stockMarket.finalProfitMultiplier
        val isProfit = finalProfit > 1.00f
        val newAttrMod = AttributeModifier(AttributeType.BUFF_DEBUFF_EFFECT, percentModifier = (finalProfit - 1f) * 100f)

        towerService.allTowers.filter { it.definition == propagandaTower }.forEach { tower ->
            tower.attrMods.remove(attrMod)
            if (isProfit) {
                tower.addAttributeModifier(newAttrMod)
            }
            towerService.recalculateAttrsSync(tower)
        }

        attrMod = newAttrMod
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        if (attrMod.percentModifier < 1f || event.tower.definition != propagandaTower) return

        event.tower.addAttributeModifier(attrMod)
    }
}
