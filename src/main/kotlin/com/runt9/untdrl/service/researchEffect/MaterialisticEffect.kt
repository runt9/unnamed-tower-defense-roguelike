package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.MaterialisticDefinition
import com.runt9.untdrl.model.loot.LootType
import com.runt9.untdrl.service.duringRun.LootService
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.roundToInt

class MaterialisticEffect(
    override val eventBus: EventBus,
    private val lootService: LootService,
    private val definition: MaterialisticDefinition
) : ResearchEffect {
    override fun apply() {
        lootService.addGoldDropMultiplier { definition.goldMultiplier }
        lootService.lootWeights[LootType.RELIC] = (lootService.lootWeights[LootType.RELIC]!! * definition.lootChanceMultiplier).roundToInt()
        lootService.lootWeights[LootType.CONSUMABLE] = (lootService.lootWeights[LootType.CONSUMABLE]!! * definition.lootChanceMultiplier).roundToInt()
        lootService.lootWeights[LootType.CORE] = (lootService.lootWeights[LootType.CORE]!! * definition.lootChanceMultiplier).roundToInt()
    }
}
