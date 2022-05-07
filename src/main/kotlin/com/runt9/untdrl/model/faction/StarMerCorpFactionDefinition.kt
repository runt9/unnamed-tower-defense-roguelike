package com.runt9.untdrl.model.faction

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.research.ResearchEffectDefinition
import com.runt9.untdrl.model.research.research
import com.runt9.untdrl.model.research.unlockTower
import com.runt9.untdrl.model.tower.definition.flamethrower
import com.runt9.untdrl.model.tower.definition.mineThrower
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.model.tower.definition.pulseCannon
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.service.factionPassiveEffect.RndBudgetEffect
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.service.researchEffect.AdvancedBallisticsEffect
import com.runt9.untdrl.service.researchEffect.AiTradingEffect
import com.runt9.untdrl.service.researchEffect.BrainstormingEffect
import com.runt9.untdrl.service.researchEffect.CarefulInvestmentsEffect
import com.runt9.untdrl.service.researchEffect.CoreSlotEffect
import com.runt9.untdrl.service.researchEffect.DividendsEffect
import com.runt9.untdrl.service.researchEffect.DontRepeatYourselfEffect
import com.runt9.untdrl.service.researchEffect.EfficientGrowthEffect
import com.runt9.untdrl.service.researchEffect.EfficientScientistsEffect
import com.runt9.untdrl.service.researchEffect.EtfsEffect
import com.runt9.untdrl.service.researchEffect.ExpandingShrapnelEffect
import com.runt9.untdrl.service.researchEffect.FastLearningEffect
import com.runt9.untdrl.service.researchEffect.GoForBrokeEffect
import com.runt9.untdrl.service.researchEffect.GoldPurseIncreaseEffect
import com.runt9.untdrl.service.researchEffect.HighYieldDividendsEffect
import com.runt9.untdrl.service.researchEffect.KineticBallisticsEffect
import com.runt9.untdrl.service.researchEffect.LootSlotIncreaseEffect
import com.runt9.untdrl.service.researchEffect.LuckyCoreValuesEffect
import com.runt9.untdrl.service.researchEffect.LuckyRarityEffect
import com.runt9.untdrl.service.researchEffect.MaterialisticEffect
import com.runt9.untdrl.service.researchEffect.MoneyTalksEffect
import com.runt9.untdrl.service.researchEffect.MoraleBoostEffect
import com.runt9.untdrl.service.researchEffect.NeuralNetworkEffect
import com.runt9.untdrl.service.researchEffect.PerformanceBonusEffect
import com.runt9.untdrl.service.researchEffect.RevenueSharingEffect
import com.runt9.untdrl.service.researchEffect.RichGetRicherEffect
import com.runt9.untdrl.service.researchEffect.ScienceFirstApproachEffect
import com.runt9.untdrl.service.researchEffect.ShrapnelEffect
import com.runt9.untdrl.service.researchEffect.SpreadTheWealthEffect

val baseFaction = faction(1, "StarMerCorp", 25) {
    startingTower(rifleTower)

    goldPassive("Stock Market", StockMarketEffect::class) {
        +"""
        StarMerCorp plays heavily in the Intergalactic economy, and as such bases their income off of the Stock Market.
        
        At the beginning of each wave, a certain amount of the Player's Gold is invested into the Stock Market, and at the end of the wave the proceeds are returned.
        
        Players may choose the amount of Gold to invest (10% to 50%) and a Risk Tolerance level (Low, Medium, or High).
        """.trimIndent()
    }

    researchPassive("R&D Budget", RndBudgetEffect::class) {
        +"""
        StarMerCorp has quite the R&D department, mostly made up of scientists "hired away" from other companies through the years.
        
        Whenever the Stock Market generates profit, a percentage of that profit is immediately converted into Research points.
        The player may choose the amount from 10% to 50%.
        """.trimIndent()
    }

    unlockTower(rocketTower, 15)
    unlockTower(propagandaTower, 25)
    unlockTower(flamethrower, 35)
    unlockTower(mineThrower, 45)
    unlockTower(pulseCannon, 50)

    val carefulInvestments = research("Careful Investments", TextureDefinition.PROJECTILE, 10) {
        +"All Stock Market risk levels have their negative minimum returns halved (e.g. -25% to -12.5%)."
        +CarefulInvestmentsEffectDefinition()
    }

    val etfs = research("ETFs", TextureDefinition.GOLD_MINE, 20) {
        +"The Stock Market cannot lose Gold two or more waves in a row. If the Stock Market would lose Gold on a consecutive wave, it rerolls until a positive profit is rolled."
        emptyDefinition(EtfsEffect::class)
        dependsOn(carefulInvestments)
    }

    val aiTrading = research("AI Trading", TextureDefinition.GOLD_MINE, 20) {
        +"Stock Market profit percentage rolls are Lucky."
        emptyDefinition(AiTradingEffect::class)
        dependsOn(etfs)
    }

    val richGetRicher = research("Rich Get Richer", TextureDefinition.GOLD_MINE, 25) {
        +"If the Stock Market would return a profit after a wave, the profit percentage is increased by +1% per 10 Gold invested at the beginning of the wave."
        +RichGetRicherEffectDefinition(10)
        dependsOn(aiTrading)
    }

    val neuralNetwork = research("Neural Network", TextureDefinition.GOLD_MINE, 35) {
        +"Stock Market risk tolerances gain +1% maximum profit per wave"
        +NeuralNetworkEffectDefinition(0.01f)
        dependsOn(aiTrading)
    }

    val goForBroke = research("Go For Broke", TextureDefinition.PROTOTYPE_TOWER, 15) {
        +"Can now invest up to 100% of gold into the Stock Market, and adds a Super High Risk investment option that ranges from -50% to +100%."
        emptyDefinition(GoForBrokeEffect::class)
    }

    val dividends = research("Dividends", TextureDefinition.GOLD_MINE, 50) {
        +"During a wave, gain Gold each second equal to 1% of the Gold amount currently invested in the Stock Market."
        +DividendsEffectDefinition(0.01f)
    }

    val highYieldDividends = research("High-Yield Dividends", TextureDefinition.GOLD_MINE, 35) {
        +"Increases the bonus for Dividends to 2.5%."
        +HighYieldDividendsEffectDefinition(0.025f)
        dependsOn(dividends)
    }

    val efficientScientists1 = research("Efficient Scientists I", TextureDefinition.RESEARCH_LAB, 10) {
        +"Increases Research gained from Stock Market Profit by 25%"
        +EfficientScientistsEffectDefinition(0.25f)
    }

    val efficientScientists2 = research("Efficient Scientists II", TextureDefinition.RESEARCH_LAB, 20) {
        +"Increases Research gained from Stock Market Profit by an additional 25%"
        +EfficientScientistsEffectDefinition(0.25f)
        dependsOn(efficientScientists1)
    }

    val performanceBonus = research("Performance Bonus", TextureDefinition.RESEARCH_LAB, 35) {
        +"Research gained from Stock Market Profit is increased by 2% per completed Research."
        +PerformanceBonusDefinition(0.02f)
        dependsOn(efficientScientists2)
    }

    val revenueSharing = research("Revenue Sharing", TextureDefinition.RESEARCH_LAB, 35) {
        +"Research gained from Stock Market Profit is by the profit percentage."
        emptyDefinition(RevenueSharingEffect::class)
        dependsOn(efficientScientists2)
    }

    val brainstorming = research("Brainstorming", TextureDefinition.RESEARCH_LAB, 15) {
        +"Grants an additional option in the Research menu and discounts all Research costs by 10%"
        +BrainstormingEffectDefinition(0.1f)
    }

    val dontRepeatYourself = research("Don't Repeat Yourself", TextureDefinition.RESEARCH_LAB, 30) {
        +"Completing a Research item discounts all other currently available Research items by 10%, up to a maximum of 50%."
        +DontRepeatYourselfDefinition(0.1f, 0.5f)
        dependsOn(brainstorming)
    }

    val scienceFirst = research("Science-first Approach", TextureDefinition.RESEARCH_LAB, 10) {
        +"Allows converting up to 100% of Stock Market profit to Research."
        emptyDefinition(ScienceFirstApproachEffect::class)
    }

    val biggerGoldPurse = research("Bigger Gold Purse", TextureDefinition.GOLD_MINE, 20) {
        +"Increases Gold purse size by +50"
        +GoldPurseIncreaseDefinition(50)
    }

    val biggestGoldPurse = research("Biggest Gold Purse", TextureDefinition.GOLD_MINE, 40) {
        +"Increases Gold purse size by +100"
        +GoldPurseIncreaseDefinition(100)
        dependsOn(biggerGoldPurse)
    }

    val luckyRarity = research("Lucky Loot", TextureDefinition.GOLD_MINE, 35) {
        +"Loot Rarity is Lucky"
        emptyDefinition(LuckyRarityEffect::class)
    }

    val luckyCoreAttributes = research("Lucky Cores", TextureDefinition.GOLD_MINE, 35) {
        +"Attribute Values of Tore Cores are Lucky"
        emptyDefinition(LuckyCoreValuesEffect::class)
        dependsOn(luckyRarity)
    }

    val biggerBackpack = research("Bigger Backpack", TextureDefinition.GOLD_MINE, 50) {
        +"Adds an additional slot for collecting loot"
        emptyDefinition(LootSlotIncreaseEffect::class)
        dependsOn(biggerGoldPurse)
    }

    val biggestBackpack = research("Biggest Backpack", TextureDefinition.GOLD_MINE, 50) {
        +"Adds an additional slot for collecting loot"
        emptyDefinition(LootSlotIncreaseEffect::class)
        dependsOn(biggerBackpack)
    }

    val materialistic = research("Materialistic", TextureDefinition.GOLD_MINE, 25) {
        +"Enemies drop 50% less gold, but have a 25% increased chance to drop Relics, Consumables, and Tower Cores"
        +MaterialisticDefinition(-0.5f, 1.25f)
        dependsOn(luckyRarity)
    }

    val spreadTheWealth = research("Spread the Wealth", TextureDefinition.GOLD_MINE, 50) {
        +"After a wave where the Stock Market produced a profit, increase looted gold amount by the profit percentage"
        emptyDefinition(SpreadTheWealthEffect::class)
        dependsOn(richGetRicher)
    }

    val fastLearning = research("Fast Learning", TextureDefinition.RESEARCH_LAB, 15) {
        +"Towers gain 25% increased XP"
        +FastLearningDefinition(0.25f)
    }

    val efficientGrowth = research("Efficient Growth", TextureDefinition.RESEARCH_LAB, 25) {
        +"Towers gain 20% increased attribute growth"
        +EfficientGrowthDefinition(0.2f)
        dependsOn(fastLearning)
    }

    val moneyTalks = research("Money Talks", TextureDefinition.GOLD_MINE, 50) {
        +"Towers gain 1% increased damage per 25 Gold currently held"
        +MoneyTalksDefinition(25)
        dependsOn(goForBroke)
    }

    val moraleBoost = research("Morale Boost", TextureDefinition.ENEMY, 35) {
        +"After killing an enemy, towers gain 10% increased attack speed for 3 seconds, stacking up to 5 times"
        +MoraleBoostDefinition(10f, 3f, 5)
        dependsOn(moneyTalks)
    }

    val shrapnel = research("Shrapnel", TextureDefinition.ENEMY, 20) {
        +"Physical damage dealt by towers has a 25% chance to apply a Bleed that deals 75% of the Physical Damage of the hit over 3s"
        +ShrapnelDefinition(0.25f, 3f, 0.75f)
    }

    val expandingShrapnel = research("Expanding Shrapnel", TextureDefinition.ENEMY, 35) {
        +"Critical hits with Physical damage refresh the duration of existing bleeds on that enemy and bleeds applied by Shrapnel deal the same damage over 50% reduced duration"
        +ExpandingShrapnelDefinition(0.5f)
        dependsOn(shrapnel)
    }

    val coreSlots1 = research("Core Slots I", TextureDefinition.ENEMY, 25) {
        +"Towers gain an additional Tower Core slot"
        emptyDefinition(CoreSlotEffect::class)
    }

    val coreSlots2 = research("Core Slots II", TextureDefinition.ENEMY, 35) {
        +"Towers gain an additional Tower Core slot"
        emptyDefinition(CoreSlotEffect::class)
        dependsOn(coreSlots1)
    }

    val advBallistics = research("Advanced Ballistics", TextureDefinition.ENEMY, 10) {
        +"All projectiles do 25% more damage, penetrate 10% of enemy resistances, and pierce an additional enemy."
        +AdvancedBallisticsEffectDefinition(0.25f, 0.1f)
    }

    val kineticBallistics = research("Kinetic Ballistics", TextureDefinition.ENEMY, 50) {
        dependsOn(advBallistics)
        +"All projectiles do an additional 25% of base damage as Energy damage and have a 5% chance to stun all enemies hit for 0.75s."
        +KineticBallisticsEffectDefinition(0.25f, 0.05f, 0.75f)
    }
}

class AdvancedBallisticsEffectDefinition(val damagePct: Float, val penetration: Float) : ResearchEffectDefinition(AdvancedBallisticsEffect::class)
class KineticBallisticsEffectDefinition(val lightningDamage: Float, val stunChance: Float, val stunDuration: Float) :
    ResearchEffectDefinition(KineticBallisticsEffect::class)
class CarefulInvestmentsEffectDefinition : ResearchEffectDefinition(CarefulInvestmentsEffect::class)
class RichGetRicherEffectDefinition(val goldPerPct: Int) : ResearchEffectDefinition(RichGetRicherEffect::class)
class NeuralNetworkEffectDefinition(val profitPctPerWave: Float) : ResearchEffectDefinition(NeuralNetworkEffect::class)
class DividendsEffectDefinition(val dividendPct: Float) : ResearchEffectDefinition(DividendsEffect::class)
class HighYieldDividendsEffectDefinition(val dividendPct: Float) : ResearchEffectDefinition(HighYieldDividendsEffect::class)
class EfficientScientistsEffectDefinition(val increasePct: Float) : ResearchEffectDefinition(EfficientScientistsEffect::class)
class BrainstormingEffectDefinition(val discountPct: Float) : ResearchEffectDefinition(BrainstormingEffect::class)
class DontRepeatYourselfDefinition(val discountPct: Float, val discountCap: Float) : ResearchEffectDefinition(DontRepeatYourselfEffect::class)
class PerformanceBonusDefinition(val bonusPct: Float) : ResearchEffectDefinition(PerformanceBonusEffect::class)
class GoldPurseIncreaseDefinition(val increaseAmt: Int) : ResearchEffectDefinition(GoldPurseIncreaseEffect::class)
class MaterialisticDefinition(val goldMultiplier: Float, val lootChanceMultiplier: Float) : ResearchEffectDefinition(MaterialisticEffect::class)
class FastLearningDefinition(val xpPercent: Float) : ResearchEffectDefinition(FastLearningEffect::class)
class EfficientGrowthDefinition(val growthPct: Float) : ResearchEffectDefinition(EfficientGrowthEffect::class)
class MoneyTalksDefinition(val goldPerDmg: Int) : ResearchEffectDefinition(MoneyTalksEffect::class)
class MoraleBoostDefinition(val attackSpeedIncrease: Float, val duration: Float, val maxStacks: Int) : ResearchEffectDefinition(MoraleBoostEffect::class)
class ShrapnelDefinition(val bleedChance: Float, val duration: Float, val pctOfPhysicalDamage: Float) : ResearchEffectDefinition(ShrapnelEffect::class)
class ExpandingShrapnelDefinition(val durationMod: Float) : ResearchEffectDefinition(ExpandingShrapnelEffect::class)
