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
import com.runt9.untdrl.service.researchEffect.EnergyBallisticsEffect

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

    val advBallistics = research("Advanced Ballistics", TextureDefinition.ENEMY, 10) {
        +"All projectiles do 25% more damage and penetrate 10% of enemy resistances."
        advancedBallistics(0.25f, 0.1f)
    }

    val energyBallistics = research("Energy Ballistics", TextureDefinition.ENEMY, 50) {
        dependsOn(advBallistics)
        +"All projectiles do an additional 25% of base damage as Lightning damage and have a 10% chance to stun all enemies hit for 0.75s."
        energyBallistics(0.25f, 0.1f, 0.75f)
    }
}

class AdvancedBallisticsEffectDefinition(val damagePct: Float, val penetration: Float) : ResearchEffectDefinition {
    override val effectClass = AdvancedBallisticsEffect::class
}

fun ResearchDefinition.Builder.advancedBallistics(damagePct: Float, penetration: Float) {
    definition = AdvancedBallisticsEffectDefinition(damagePct, penetration)
}

class EnergyBallisticsEffectDefinition(val lightningDamage: Float, val stunChance: Float, val stunDuration: Float) : ResearchEffectDefinition {
    override val effectClass = EnergyBallisticsEffect::class
}

fun ResearchDefinition.Builder.energyBallistics(lightningDamage: Float, stunChance: Float, stunDuration: Float) {
    definition = EnergyBallisticsEffectDefinition(lightningDamage, stunChance, stunDuration)
}
