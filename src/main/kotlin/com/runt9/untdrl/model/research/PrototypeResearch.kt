package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.UnitTexture

val advBallistics = research("Advanced Ballistics", UnitTexture.ENEMY, 10) {
    +"All projectiles do 25% more damage and penetrate 10% of enemy resistances."
    advancedBallistics(0.25f, 0.1f)
}

val energyBallistics = research("Energy Ballistics", UnitTexture.ENEMY, 50) {
    dependsOn(advBallistics)
    +"All projectiles do an additional 25% of base damage as Lightning damage and have a 10% chance to stun all enemies hit for 0.75s."
    energyBallistics(0.25f, 0.1f, 0.75f)
}

val allResearch = listOf(advBallistics, energyBallistics)
