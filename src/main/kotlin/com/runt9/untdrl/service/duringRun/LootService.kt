package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.loot.BuildingCore
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.LootPool
import com.runt9.untdrl.model.loot.LootType
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class LootService(private val eventBus: EventBus, registry: RunServiceRegistry, private val randomizerService: RandomizerService, private val runStateService: RunStateService) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()

    private val lootWeights = mapOf(
        LootType.GOLD to 90,
        LootType.CONSUMABLE to 5,
        LootType.CORE to 4,
        LootType.RELIC to 1
    )

    private val lootTable = generateLootTable()
    val lootPool = LootPool()

    @HandlesEvent
    fun enemyKilled(event: EnemyRemovedEvent) {
        logger.info { "Enemy removed event" }
        if (!event.wasKilled) return

        generateLoot()
    }

    private fun generateLoot() = runOnServiceThread {
        randomizerService.randomize {
            when (lootTable.random(it)) {
                LootType.GOLD -> generateGold()
                LootType.RELIC -> generateRelic()
                LootType.CONSUMABLE -> generateConsumable()
                LootType.CORE -> generateCore()
            }
        }
    }

    private fun generateRelic() {
        logger.info { "Generating relic" }
        lootPool.items += Relic()
    }

    private fun generateConsumable() {
        logger.info { "Generating consumable" }
        lootPool.items += Consumable()
    }

    private fun generateCore() {
        logger.info { "Generating core" }
        lootPool.items += BuildingCore()
    }

    private fun generateLootTable(): List<LootType> {
        val lootTable = mutableListOf<LootType>()

        lootWeights.forEach { (type, weight) ->
            repeat(weight) { lootTable.add(type) }
        }

        return lootTable.toList()
    }

    private fun generateGold() {
        val wave = runStateService.load().wave
        logger.info { "Generating $wave gold" }
        lootPool.gold += wave
    }
}
