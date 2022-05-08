package com.runt9.untdrl.model.faction

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.research.ResearchEffectDefinition
import com.runt9.untdrl.model.research.research
import com.runt9.untdrl.model.research.unlockTower
import com.runt9.untdrl.model.tower.definition.MentalDisruptionDefinition
import com.runt9.untdrl.model.tower.definition.MinigunSpecialization
import com.runt9.untdrl.model.tower.definition.MissileSwarmSpecialization
import com.runt9.untdrl.model.tower.definition.NapalmCannonSpecialization
import com.runt9.untdrl.model.tower.definition.RiseToTheOccasionDefinition
import com.runt9.untdrl.model.tower.definition.SayItLouderDefinition
import com.runt9.untdrl.model.tower.definition.ShotgunSpecialization
import com.runt9.untdrl.model.tower.definition.SniperSpecialization
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
import com.runt9.untdrl.service.researchEffect.AssassinationEffect
import com.runt9.untdrl.service.researchEffect.BrainstormingEffect
import com.runt9.untdrl.service.researchEffect.CarefulInvestmentsEffect
import com.runt9.untdrl.service.researchEffect.ChainExplosionsEffect
import com.runt9.untdrl.service.researchEffect.CoreSlotEffect
import com.runt9.untdrl.service.researchEffect.DefectorsEffect
import com.runt9.untdrl.service.researchEffect.DividendsEffect
import com.runt9.untdrl.service.researchEffect.DontRepeatYourselfEffect
import com.runt9.untdrl.service.researchEffect.EfficientGrowthEffect
import com.runt9.untdrl.service.researchEffect.EfficientScientistsEffect
import com.runt9.untdrl.service.researchEffect.EntertainmentNewsEffect
import com.runt9.untdrl.service.researchEffect.EtfsEffect
import com.runt9.untdrl.service.researchEffect.ExpandingShrapnelEffect
import com.runt9.untdrl.service.researchEffect.FastLearningEffect
import com.runt9.untdrl.service.researchEffect.FireSpreadsEffect
import com.runt9.untdrl.service.researchEffect.GoForBrokeEffect
import com.runt9.untdrl.service.researchEffect.GoldPurseIncreaseEffect
import com.runt9.untdrl.service.researchEffect.GrowTheCultEffect
import com.runt9.untdrl.service.researchEffect.HappyShareholdersEffect
import com.runt9.untdrl.service.researchEffect.HeRoundsEffect
import com.runt9.untdrl.service.researchEffect.HeatRoundsEffect
import com.runt9.untdrl.service.researchEffect.HighYieldDividendsEffect
import com.runt9.untdrl.service.researchEffect.HollowPointEffect
import com.runt9.untdrl.service.researchEffect.KineticBallisticsEffect
import com.runt9.untdrl.service.researchEffect.LootSlotIncreaseEffect
import com.runt9.untdrl.service.researchEffect.LuckyCoreValuesEffect
import com.runt9.untdrl.service.researchEffect.LuckyRarityEffect
import com.runt9.untdrl.service.researchEffect.MaterialisticEffect
import com.runt9.untdrl.service.researchEffect.MlrsEffect
import com.runt9.untdrl.service.researchEffect.MoneyIsEverythingEffect
import com.runt9.untdrl.service.researchEffect.MoneyTalksEffect
import com.runt9.untdrl.service.researchEffect.MoraleBoostEffect
import com.runt9.untdrl.service.researchEffect.NeuralNetworkEffect
import com.runt9.untdrl.service.researchEffect.PerformanceBonusEffect
import com.runt9.untdrl.service.researchEffect.PointOfImpactEffect
import com.runt9.untdrl.service.researchEffect.PowerInNumbersEffect
import com.runt9.untdrl.service.researchEffect.PrecisionEffect
import com.runt9.untdrl.service.researchEffect.RailgunsEffect
import com.runt9.untdrl.service.researchEffect.RevenueSharingEffect
import com.runt9.untdrl.service.researchEffect.RichGetRicherEffect
import com.runt9.untdrl.service.researchEffect.SaltInTheWoundEffect
import com.runt9.untdrl.service.researchEffect.ScienceFirstApproachEffect
import com.runt9.untdrl.service.researchEffect.ShrapnelEffect
import com.runt9.untdrl.service.researchEffect.SpottersEffect
import com.runt9.untdrl.service.researchEffect.SpreadTheWealthEffect
import com.runt9.untdrl.service.researchEffect.TwelveGaugeEffect
import com.runt9.untdrl.service.researchEffect.UndyingFervorEffect
import com.runt9.untdrl.service.researchEffect.VulcanCannonsEffect

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

    val rocketUnlock = unlockTower(rocketTower, 15)
    val propagandaUnlock = unlockTower(propagandaTower, 25)
    val flamethrowerUnlock = unlockTower(flamethrower, 35)
    val mineThrowerUnlock = unlockTower(mineThrower, 45)
    val pulseCannonUnlock = unlockTower(pulseCannon, 50)

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

    val hollowPoint = research("Hollow Point", TextureDefinition.ENEMY, 30) {
        +"Rifle Towers do an additional 1.5x damage with Bleed"
        +HollowPointDefinition(0.5f)
        dependsOn(shrapnel)
    }

    val advBallistics = research("Advanced Ballistics", TextureDefinition.ENEMY, 10) {
        +"All projectiles do 25% more damage, penetrate 10% of enemy resistances, and pierce an additional enemy."
        +AdvancedBallisticsEffectDefinition(0.25f, 0.1f)
    }

    val precision = research("Precision", TextureDefinition.PROJECTILE, 45) {
        +"Rifle Towers gain a stacking +1% crit chance bonus on non-crit. Bonus resets on crit."
        +PrecisionDefinition(0.01f)
        dependsOn(advBallistics)
    }

    val saltInTheWound = research("Salt in the Wound", TextureDefinition.ENEMY, 50) {
        +"When a Rifle Tower crits an enemy affected by a bleed, the bleed with the highest remaining damage is removed and the remaining damage is dealt instantly and increased by 25%"
        +SaltInTheWoundDefinition(0.25f)
        dependsOn(hollowPoint, precision)
    }

    val kineticBallistics = research("Kinetic Ballistics", TextureDefinition.ENEMY, 50) {
        +"All projectiles do an additional 25% of base damage as Energy damage and have a 5% chance to stun all enemies hit for 0.75s."
        +KineticBallisticsEffectDefinition(0.25f, 0.05f, 0.75f)
        dependsOn(advBallistics)
    }

    val railguns = research("Railguns", TextureDefinition.PROJECTILE, 40) {
        +"Sniper towers deal 50% increased damage and their bullets pierce all enemies"
        +RailgunsDefinition(50f)
        dependsOn(kineticBallistics)
        dependsOn(SniperSpecialization::class)
    }

    val assassination = research("Assassination", TextureDefinition.ENEMY, 30) {
        +"Sniper towers gain 2% increased crit chance per 1% enemy missing HP"
        +AssassinationDefinition(0.02f)
        dependsOn(SniperSpecialization::class)
    }

    val vulcanCannons = research("Vulcan Cannons", TextureDefinition.PROTOTYPE_TOWER, 50) {
        +"Minigun towers penetrate 5% of enemy Physical resistance per stack of the Minigun's attack speed buff"
        +VulcanCannonsDefinition(0.05f)
        dependsOn(advBallistics)
        dependsOn(MinigunSpecialization::class)
    }

    val twelveGauge = research("12 Gauge", TextureDefinition.PROTOTYPE_TOWER, 20) {
        +"Shotgun towers have 20% reduced firing arc, fire 2 additional projectiles, and deal 25% increased damage"
        +TwelveGaugeDefinition(0.2f, 2, 25f)
        dependsOn(ShotgunSpecialization::class)
    }

    val mlrs = research("MLRS", TextureDefinition.PROTOTYPE_TOWER, 35) {
        +"Missile Swarm towers lose homing but have 200% increased projectile count"
        +MlrsDefinition(200f)
        dependsOn(rocketUnlock)
        dependsOn(MissileSwarmSpecialization::class)
    }

    val fireSpreads = research("Fire Spreads", TextureDefinition.ENEMY, 50) {
        +"When an enemy dies while affected by a burn from a Napalm Cannon, that burn spreads to nearby enemies."
        emptyDefinition(FireSpreadsEffect::class)
        dependsOn(rocketUnlock)
        dependsOn(NapalmCannonSpecialization::class)
    }

    val spotters = research("Spotters", TextureDefinition.PROJECTILE, 20) {
        +"Rocket towers gain 10% increased range each second not firing. Bonus is lost after firing a shot"
        +SpottersDefinition(10f)
        dependsOn(rocketUnlock)
    }

    val heRounds = research("HE Rounds", TextureDefinition.ENEMY, 15) {
        +"Rocket Towers gain 25% increased AoE and Damage"
        +HeRoundsDefinition(25f)
        dependsOn(rocketUnlock)
    }

    val heatRounds = research("HEAT Rounds", TextureDefinition.ENEMY, 35) {
        +"Rocket Towers gain 25% increased Damage and penetrate 20% of enemy Physical and Heat resistances"
        +HeatRoundsDefinition(25f, 0.2f)
        dependsOn(rocketUnlock, heRounds)
    }

    val pointOfImpact = research("Point of Impact", TextureDefinition.PROTOTYPE_TOWER, 40) {
        +"Damage dealt to enemies by Rocket Towers is increased by 50% at the point of impact, decreasing to 0% at the edge of the explosion"
        +PointOfImpactDefinition(0.5f)
        dependsOn(rocketUnlock, heRounds)
    }

    val chainExplosions = research("Chain Explosions", TextureDefinition.ENEMY, 50) {
        +"Enemies killed by Rocket towers explode, dealing 25% of their maximum HP as Physical and Heat damage to nearby enemies"
        +ChainExplosionsDefinition(0.25f)
        dependsOn(rocketUnlock, heatRounds)
    }

    val happyShareholders = research("Happy Shareholders", TextureDefinition.GOLD_MINE, 25) {
        +"Whenever the Stock Market returns a profit, Propaganda Towers gain an increase to Buff/Debuff effect equal to the profit percentage for the next wave"
        emptyDefinition(HappyShareholdersEffect::class)
        dependsOn(propagandaUnlock)
    }

    val moneyIsEverything = research("Money is Everything", TextureDefinition.GOLD_MINE, 50) {
        +"Propaganda towers permanently gain +2% Buff/Debuff Effect whenever the Stock Market returns a profit"
        +MoneyIsEverythingDefinition(0.02f)
        dependsOn(propagandaUnlock, happyShareholders)
    }

    val defectors = research("Defectors", TextureDefinition.ENEMY, 40) {
        +"Enemies get stunned for 0.5s the first time they are affected by any Say it Louder effect"
        +DefectorsDefinition(0.5f)
        dependsOn(propagandaUnlock)
        dependsOn(SayItLouderDefinition::class)
    }

    val undyingFervor = research("Undying Fervor", TextureDefinition.ENEMY, 50) {
        +"Rise to the Occasion’s bonus doesn't completely reset at the end of a wave, instead dropping down to 10% of its current bonus"
        +UndyingFervorDefinition(0.1f)
        dependsOn(propagandaUnlock)
        dependsOn(RiseToTheOccasionDefinition::class)
    }

    val entertainmentNews = research("Entertainment News", TextureDefinition.RESEARCH_LAB, 35) {
        +"Towers affected by Mental Disruption’s bonus also apply a non-stacking DoT effect to enemies hit, dealing 50% of the total damage of the hit as Mystic damage over 2s"
        +EntertainmentNewsDefinition(0.5f, 2f)
        dependsOn(propagandaUnlock)
        dependsOn(MentalDisruptionDefinition::class)
    }

    val growTheCult = research("Grow the Cult", TextureDefinition.ENEMY, 45) {
        +"Research gained from Stock Market Profit is increased by 10% per Propaganda Tower"
        +GrowTheCultDefinition(0.1f)
        dependsOn(propagandaUnlock, efficientScientists2)
    }

    val powerInNumbers = research("Power in Numbers", TextureDefinition.PROTOTYPE_TOWER, 30) {
        +"Propaganda Towers gain 5% increased Buff/Debuff Effect for each tower in range"
        +PowerInNumbersDefinition(5f)
        dependsOn(propagandaUnlock)
    }
}

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
class HollowPointDefinition(val bleedDmgMulti: Float) : ResearchEffectDefinition(HollowPointEffect::class)
class AdvancedBallisticsEffectDefinition(val damagePct: Float, val penetration: Float) : ResearchEffectDefinition(AdvancedBallisticsEffect::class)
class PrecisionDefinition(val critBonus: Float) : ResearchEffectDefinition(PrecisionEffect::class)
class SaltInTheWoundDefinition(val damageMultiplier: Float) : ResearchEffectDefinition(SaltInTheWoundEffect::class)
class KineticBallisticsEffectDefinition(val lightningDamage: Float, val stunChance: Float, val stunDuration: Float) : ResearchEffectDefinition(KineticBallisticsEffect::class)
class RailgunsDefinition(val damageIncrease: Float) : ResearchEffectDefinition(RailgunsEffect::class)
class AssassinationDefinition(val critPerMissingHp: Float) : ResearchEffectDefinition(AssassinationEffect::class)
class VulcanCannonsDefinition(val penPerStack: Float) : ResearchEffectDefinition(VulcanCannonsEffect::class)
class TwelveGaugeDefinition(val arcReduction: Float, val bonusProj: Int, val damageIncrease: Float) : ResearchEffectDefinition(TwelveGaugeEffect::class)
class MlrsDefinition(val projIncrease: Float) : ResearchEffectDefinition(MlrsEffect::class)
class SpottersDefinition(val rangeIncrease: Float) : ResearchEffectDefinition(SpottersEffect::class)
class HeRoundsDefinition(val attrIncrease: Float) : ResearchEffectDefinition(HeRoundsEffect::class)
class HeatRoundsDefinition(val damageIncrease: Float, val penetration: Float) : ResearchEffectDefinition(HeatRoundsEffect::class)
class PointOfImpactDefinition(val damageIncrease: Float) : ResearchEffectDefinition(PointOfImpactEffect::class)
class ChainExplosionsDefinition(val lifePct: Float) : ResearchEffectDefinition(ChainExplosionsEffect::class)
class MoneyIsEverythingDefinition(val buffEffectAmt: Float) : ResearchEffectDefinition(MoneyIsEverythingEffect::class)
class DefectorsDefinition(val stunDuration: Float) : ResearchEffectDefinition(DefectorsEffect::class)
class UndyingFervorDefinition(val remainingBonus: Float) : ResearchEffectDefinition(UndyingFervorEffect::class)
class EntertainmentNewsDefinition(val hitDamagePct: Float, val duration: Float) : ResearchEffectDefinition(EntertainmentNewsEffect::class)
class GrowTheCultDefinition(val researchPerTower: Float) : ResearchEffectDefinition(GrowTheCultEffect::class)
class PowerInNumbersDefinition(val buffEffectPerTower: Float) : ResearchEffectDefinition(PowerInNumbersEffect::class)
