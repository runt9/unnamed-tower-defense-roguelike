package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.model.loot.LootPool
import com.runt9.untdrl.model.loot.LootType
import com.runt9.untdrl.model.loot.Rarity
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.Shop
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

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

    override fun startInternal() {
        runStateService.update {
            currentShop = generateShop()
        }
    }

    fun generateShop(): Shop {
        val shop = Shop()
        repeat(5) { shop.relics += generateShopItem { generateRelic() } }
        repeat(5) { shop.consumables += generateShopItem { generateConsumable() } }
        repeat(5) { shop.cores += generateShopItem { generateCore() } }
        return shop
    }

    private fun <T : LootItem> generateShopItem(generateFn: LootService.() -> T): Pair<T, Int> {
        val item = generateFn()
        val baseCost = item.type.baseCost
        val finalCost = ((randomizerService.rng.nextInt(90, 111).toFloat() / 100f) * (baseCost * item.rarity.costMultiplier)).roundToInt()
        return Pair(item, finalCost)
    }

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
                LootType.RELIC -> lootPool.items += generateRelic()
                LootType.CONSUMABLE -> lootPool.items += generateConsumable()
                LootType.CORE -> lootPool.items += generateCore()
            }
        }
    }

    private fun generateRelic(): Relic {
        logger.info { "Generating relic" }
        return Relic(generateRarity())
    }

    private fun generateConsumable(): Consumable {
        logger.info { "Generating consumable" }
        return Consumable(generateRarity())
    }

    private fun generateCore(): TowerCore {
        logger.info { "Generating core" }
        return TowerCore(generateRarity())
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

    private fun generateRarity(): Rarity {
        val roll = randomizerService.rng.nextInt(0, 100)

        return when {
            roll >= 95 -> Rarity.LEGENDARY
            roll >= 80 -> Rarity.RARE
            roll >= 50 -> Rarity.UNCOMMON
            else -> Rarity.COMMON
        }
    }
}
