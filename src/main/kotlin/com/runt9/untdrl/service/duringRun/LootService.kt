package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.model.loot.LootPool
import com.runt9.untdrl.model.loot.LootType
import com.runt9.untdrl.model.loot.Rarity
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.Shop
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.loot.definition.ConsumableActionDefinition
import com.runt9.untdrl.model.loot.definition.availableConsumables
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class LootService(
    eventBus: EventBus,
    registry: RunServiceRegistry,
    private val randomizer: RandomizerService,
    private val runStateService: RunStateService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()

    private val lootWeights = mapOf(
        LootType.GOLD to 90,
        LootType.CONSUMABLE to 500,
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
        val finalCost = ((randomizer.rng.nextInt(90, 111).toFloat() / 100f) * (baseCost * item.rarity.costMultiplier)).roundToInt()
        return Pair(item, finalCost)
    }

    @HandlesEvent
    fun enemyKilled(event: EnemyRemovedEvent) = launchOnServiceThread {
        logger.info { "Enemy removed event" }
        if (!event.wasKilled) return@launchOnServiceThread

        generateLoot()
    }

    private fun generateLoot() = launchOnServiceThread {
        randomizer.randomize {
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
        val rarity = generateRarity()
        val definition = availableConsumables[rarity]!!.random()
        val consumable = Consumable(rarity, definition)
        consumable.action = dynamicInject(
            consumable.definition.action.actionClass,
            { c: Class<*> -> c.interfaces.contains(ConsumableActionDefinition::class.java) } to definition.action
        )
        return consumable
    }

    private fun generateCore(): TowerCore {
        val rarity = generateRarity()
        val count = rarity.numCoreAttrs
        val allowedAttributeTypes = runStateService.load().availableBuildings.flatMap { it.attrs.keys }.distinct()

        val generatedSoFar = mutableListOf<AttributeType>()
        val modifiers = mutableListOf<AttributeModifier>()

        repeat(count) {
            val type = allowedAttributeTypes.filter { !generatedSoFar.contains(it) }.random(randomizer.rng)

            modifiers += randomizer.randomAttributeModifier(type)
            generatedSoFar += type
        }

        // TODO: Legendary passives
//        val passive = if (rarity == Rarity.LEGENDARY) passiveService.randomPassive(rarity) else null

        val core = TowerCore(rarity, modifiers)
        logger.info { "Generated core:\n${core.description}" }
        return core
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
        val roll = randomizer.rng.nextInt(0, 100)

        return when {
            roll >= 95 -> Rarity.LEGENDARY
            roll >= 80 -> Rarity.RARE
            roll >= 50 -> Rarity.UNCOMMON
            else -> Rarity.COMMON
        }
    }

    private val Rarity.numCoreAttrs: Int get() = when (this) {
        Rarity.COMMON -> 1
        Rarity.UNCOMMON -> 2
        Rarity.RARE, Rarity.LEGENDARY -> 3
    }
}
