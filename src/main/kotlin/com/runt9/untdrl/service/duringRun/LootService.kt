package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.model.loot.LootPool
import com.runt9.untdrl.model.loot.LootType
import com.runt9.untdrl.model.loot.Rarity
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.Shop
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.loot.definition.ConsumableActionDefinition
import com.runt9.untdrl.model.loot.definition.RelicDefinition
import com.runt9.untdrl.model.loot.definition.RelicEffectDefinition
import com.runt9.untdrl.model.loot.definition.availableConsumables
import com.runt9.untdrl.model.loot.definition.availableLegendaryPassives
import com.runt9.untdrl.model.loot.definition.availableRelics
import com.runt9.untdrl.model.loot.definition.initConsumables
import com.runt9.untdrl.model.loot.definition.initRelics
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.consumableAction.ConsumableAction
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.dynamicInjectCheckIsSubclassOf
import com.runt9.untdrl.util.ext.generateWeightedList
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.view.duringRun.SHOP_ITEMS
import kotlin.math.roundToInt

class LootService(
    eventBus: EventBus,
    registry: RunServiceRegistry,
    private val randomizer: RandomizerService,
    private val runStateService: RunStateService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()

    val lootWeights = mutableMapOf(
        LootType.GOLD to 90,
        LootType.CONSUMABLE to 5,
        LootType.CORE to 4,
        LootType.RELIC to 1
    )

    private val rarityWeights = mapOf(
        Rarity.COMMON to 50,
        Rarity.UNCOMMON to 30,
        Rarity.RARE to 15,
        Rarity.LEGENDARY to 5
    )

    private val rarityTable = generateWeightedList(rarityWeights)
    private val lootTable = generateWeightedList(lootWeights)
    val lootPool = LootPool()
    private val currentlyGeneratedRelics = mutableListOf<RelicDefinition>()

    var luckyRarity = false
    var luckyCoreAttributes = false
    private val goldDropMultipliers = mutableListOf<() -> Float>()
    private val lootedGoldMultipliers = mutableListOf<() -> Float>()
    private val currentlyAppliedConsumables = mutableListOf<ConsumableAction>()

    fun addGoldDropMultiplier(multiplier: () -> Float) {
        goldDropMultipliers += multiplier
    }

    fun addLootedGoldMultiplier(multiplier: () -> Float) {
        lootedGoldMultipliers += multiplier
    }

    override fun startInternal() {
        initConsumables()
        initRelics()
        runStateService.update {
            currentShop = generateShop()
        }
    }

    fun generateShop(currentShop: Shop = Shop()): Shop {
        currentlyGeneratedRelics -= currentShop.relics.keys.map(Relic::definition).toSet()
        val shop = Shop()
        repeat(SHOP_ITEMS) {
            generateRelic()?.also { relic ->
                shop.relics += generateShopItem { relic }
                currentlyGeneratedRelics += relic.definition
            }
        }
        repeat(SHOP_ITEMS) { shop.consumables += generateShopItem { generateConsumable() } }
        repeat(SHOP_ITEMS) { shop.cores += generateShopItem { generateCore() } }
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
        if (!event.wasKilled) return@launchOnServiceThread

        generateLoot()
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        currentlyAppliedConsumables.forEach(ConsumableAction::remove)
        currentlyAppliedConsumables.clear()
    }

    private fun generateLoot() = launchOnServiceThread {
        when (lootTable.random(randomizer.rng)) {
            LootType.GOLD -> generateGold()
            LootType.RELIC -> generateRelic()?.also { relic ->
                lootPool.items += relic
                currentlyGeneratedRelics += relic.definition
            }
            LootType.CONSUMABLE -> lootPool.items += generateConsumable()
            LootType.CORE -> lootPool.items += generateCore()
        }
    }

    private fun generateRelic(): Relic? {
        logger.debug { "Generating relic" }
        val rarity = generateRarity()
        val possibleRelics = availableRelics[rarity]!!.filter { !currentlyGeneratedRelics.contains(it) }
        if (possibleRelics.isEmpty()) {
            logger.info { "Could not generate relic, all relics have been generated" }
            return null
        }

        val definition = possibleRelics.random()
        val relic = Relic(rarity, definition)
        relic.effect = dynamicInject(
            relic.definition.effect.effectClass,
            dynamicInjectCheckIsSubclassOf(RelicEffectDefinition::class.java) to definition.effect
        )
        return relic
    }

    private fun generateConsumable(): Consumable {
        logger.debug { "Generating consumable" }
        val rarity = generateRarity()
        val definition = availableConsumables[rarity]!!.random()
        val consumable = Consumable(rarity, definition)
        consumable.action = dynamicInject(
            consumable.definition.action.actionClass,
            dynamicInjectCheckIsSubclassOf(ConsumableActionDefinition::class.java) to definition.action
        )
        return consumable
    }

    private fun generateCore(): TowerCore {
        val rarity = generateRarity()
        val count = rarity.numCoreAttrs
        // TODO: If a faction's starting tower only has 2 attributes, this cannot generate a Rare/Legendary with 3 attributes
        val allowedAttributeTypes = runStateService.load().availableTowers.flatMap { it.attrs.keys }.distinct()

        val generatedSoFar = mutableListOf<AttributeType>()
        val modifiers = mutableListOf<AttributeModifier>()

        repeat(count) {
            val type = allowedAttributeTypes.filter { !generatedSoFar.contains(it) }.random(randomizer.rng)

            modifiers += randomizer.randomAttributeModifier(luckyCoreAttributes, type)
            generatedSoFar += type
        }

        val passive = if (rarity == Rarity.LEGENDARY) availableLegendaryPassives.random(randomizer.rng) else null

        return TowerCore(rarity, modifiers, passive)
    }

    private fun generateGold() {
        val generatedGold = (2..5).random(randomizer.rng)
        val goldMultiplier = goldDropMultipliers.map { it() }.sum()
        lootPool.gold += (generatedGold * (1 + goldMultiplier)).roundToInt()
        logger.debug { "Generated $generatedGold gold" }
    }

    private fun generateRarity() = randomizer.randomize(luckyRarity) { rarityTable.random(it) }

    private fun clearRemainingLootPool() {
        currentlyGeneratedRelics -= lootPool.items.filterIsInstance<Relic>().map(Relic::definition).toSet()
        lootPool.clear()
    }

    fun takeLoot(lootedGold: Int, lootItems: List<LootItem>) {
        runStateService.update {
            val goldMultiplier = lootedGoldMultipliers.map { it() }.sum()
            gold += (lootedGold * (1 + goldMultiplier)).roundToInt()

            lootItems.forEach { item ->
                when (item) {
                    is Relic -> {
                        relics += item
                        item.effect.apply()
                    }
                    is Consumable -> consumables += item
                    is TowerCore -> cores += item
                }
            }
        }

        clearRemainingLootPool()
    }

    fun useConsumable(consumable: Consumable): Boolean {
        if (!consumable.action.canApply()) return false

        consumable.action.apply()
        currentlyAppliedConsumables += consumable.action

        runStateService.update {
            consumables -= consumable
        }

        return true
    }

    private val Rarity.numCoreAttrs: Int get() = when (this) {
        Rarity.COMMON -> 1
        Rarity.UNCOMMON -> 2
        Rarity.RARE, Rarity.LEGENDARY -> 3
    }
}
