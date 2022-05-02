package com.runt9.untdrl.model.faction

import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.research.ResearchDefinition
import com.runt9.untdrl.model.research.ResearchEffectDefinition
import com.runt9.untdrl.model.research.research
import com.runt9.untdrl.model.research.unlockTower
import com.runt9.untdrl.model.tower.definition.flamethrower
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.service.factionPassiveEffect.RnDBudgetEffect
import com.runt9.untdrl.service.factionPassiveEffect.StockMarketEffect
import com.runt9.untdrl.service.researchEffect.AdvancedBallisticsEffect
import com.runt9.untdrl.service.researchEffect.AiTradingEffect
import com.runt9.untdrl.service.researchEffect.CarefulInvestmentsEffect
import com.runt9.untdrl.service.researchEffect.DividendsEffect
import com.runt9.untdrl.service.researchEffect.EtfsEffect
import com.runt9.untdrl.service.researchEffect.GoForBrokeEffect
import com.runt9.untdrl.service.researchEffect.HighYieldDividendsEffect
import com.runt9.untdrl.service.researchEffect.KineticBallisticsEffect
import com.runt9.untdrl.service.researchEffect.NeuralNetworkEffect
import com.runt9.untdrl.service.researchEffect.RichGetRicherEffect

val baseFaction = faction(1, "StarMerCorp", 25) {
    startingTower(rifleTower)

    goldPassive("Stock Market", StockMarketEffect::class) {
        +"""
        StarMerCorp plays heavily in the Intergalactic economy, and as such bases their income off of the Stock Market.
        
        At the beginning of each wave, a certain amount of the Player's Gold is invested into the Stock Market, and at the end of the wave the proceeds are returned.
        
        Players may choose the amount of Gold to invest (10% to 50%) and a Risk Tolerance level (Low, Medium, or High).
        """.trimIndent()
    }

    researchPassive("R&D Budget", RnDBudgetEffect::class) {
        +"""
        StarMerCorp has quite the R&D department, mostly made up of scientists "hired away" from other companies through the years.
        
        Whenever the Stock Market generates profit, a percentage of that profit is immediately converted into Research points.
        The player may choose the amount from 10% to 50%.
        """.trimIndent()
    }

    unlockTower(rocketTower, 15)
    unlockTower(propagandaTower, 25)
    unlockTower(flamethrower, 35)

    val carefulInvestments = research("Careful Investments", TextureDefinition.PROJECTILE, 0) {
        +"All Stock Market risk levels have their negative minimum returns halved (e.g. -25% to -12.5%)."
        carefulInvestments()
    }

    val etfs = research("ETFs", TextureDefinition.GOLD_MINE, 0) {
        +"The Stock Market cannot lose Gold two or more waves in a row. If the Stock Market would lose Gold on a consecutive wave, it rerolls until a positive profit is rolled."
        etfs()
        dependsOn(carefulInvestments)
    }

    val aiTrading = research("AI Trading", TextureDefinition.GOLD_MINE, 0) {
        +"Stock Market profit percentage rolls are Lucky."
        aiTrading()
        dependsOn(etfs)
    }

    val richGetRicher = research("Rich Get Richer", TextureDefinition.GOLD_MINE, 0) {
        +"If the Stock Market would return a profit after a wave, the profit percentage is increased by +1% per 10 Gold invested at the beginning of the wave."
        richGetRicher(10)
        dependsOn(aiTrading)
    }

    val neuralNetwork = research("Neural Network", TextureDefinition.GOLD_MINE, 0) {
        +"Stock Market risk tolerances gain +1% maximum profit per wave"
        neuralNetwork(0.01f)
        dependsOn(aiTrading)
    }

//    val goForBroke = research("Go For Broke", TextureDefinition.PROTOTYPE_TOWER, 15) {
//        +"Can now invest up to 100% of gold into the Stock Market, and adds a Super High Risk investment option that ranges from -50% to +100%."
//        goForBroke()
//    }

//    val dividends = research("Dividends", TextureDefinition.GOLD_MINE, 0) {
//        +"During a wave, gain Gold each second equal to 1% of the Gold amount currently invested in the Stock Market."
//        dividends(0.01f)
//    }
//
//    val highYieldDividends = research("High-Yield Dividends", TextureDefinition.GOLD_MINE, 0) {
//        +"Increases the bonus for Dividends to 2.5%."
//        highYieldDividends(0.025f)
//        dependsOn(dividends)
//    }

//    val advBallistics = research("Advanced Ballistics", TextureDefinition.ENEMY, 10) {
//        +"All projectiles do 25% more damage, penetrate 10% of enemy resistances, and pierce an additional enemy."
//        advancedBallistics(0.25f, 0.1f)
//    }
//
//    val kineticBallistics = research("Kinetic Ballistics", TextureDefinition.ENEMY, 50) {
//        dependsOn(advBallistics)
//        +"All projectiles do an additional 25% of base damage as Energy damage and have a 5% chance to stun all enemies hit for 0.75s."
//        kineticBallistics(0.25f, 0.05f, 0.75f)
//    }
}

class AdvancedBallisticsEffectDefinition(val damagePct: Float, val penetration: Float) : ResearchEffectDefinition { override val effectClass = AdvancedBallisticsEffect::class }
fun ResearchDefinition.Builder.advancedBallistics(damagePct: Float, penetration: Float) { definition = AdvancedBallisticsEffectDefinition(damagePct, penetration) }

class KineticBallisticsEffectDefinition(val lightningDamage: Float, val stunChance: Float, val stunDuration: Float) : ResearchEffectDefinition { override val effectClass = KineticBallisticsEffect::class }
fun ResearchDefinition.Builder.kineticBallistics(lightningDamage: Float, stunChance: Float, stunDuration: Float) { definition = KineticBallisticsEffectDefinition(lightningDamage, stunChance, stunDuration) }

class CarefulInvestmentsEffectDefinition : ResearchEffectDefinition { override val effectClass = CarefulInvestmentsEffect::class }
fun ResearchDefinition.Builder.carefulInvestments() { definition = CarefulInvestmentsEffectDefinition() }

class EtfsEffectDefinition : ResearchEffectDefinition { override val effectClass = EtfsEffect::class }
fun ResearchDefinition.Builder.etfs() { definition = EtfsEffectDefinition() }

class AiTradingEffectDefinition : ResearchEffectDefinition { override val effectClass = AiTradingEffect::class }
fun ResearchDefinition.Builder.aiTrading() { definition = AiTradingEffectDefinition() }

class RichGetRicherEffectDefinition(val goldPerPct: Int) : ResearchEffectDefinition { override val effectClass = RichGetRicherEffect::class }
fun ResearchDefinition.Builder.richGetRicher(goldPerPct: Int) { definition = RichGetRicherEffectDefinition(goldPerPct) }

class NeuralNetworkEffectDefinition(val profitPctPerWave: Float) : ResearchEffectDefinition { override val effectClass = NeuralNetworkEffect::class }
fun ResearchDefinition.Builder.neuralNetwork(profitPctPerWave: Float) { definition = NeuralNetworkEffectDefinition(profitPctPerWave) }

class GoForBrokeEffectDefinition : ResearchEffectDefinition { override val effectClass = GoForBrokeEffect::class }
fun ResearchDefinition.Builder.goForBroke() { definition = GoForBrokeEffectDefinition() }

class DividendsEffectDefinition(val dividendPct: Float) : ResearchEffectDefinition { override val effectClass = DividendsEffect::class }
fun ResearchDefinition.Builder.dividends(dividendPct: Float) { definition = DividendsEffectDefinition(dividendPct) }

class HighYieldDividendsEffectDefinition(val dividendPct: Float) : ResearchEffectDefinition { override val effectClass = HighYieldDividendsEffect::class }
fun ResearchDefinition.Builder.highYieldDividends(dividendPct: Float) { definition = HighYieldDividendsEffectDefinition(dividendPct) }
