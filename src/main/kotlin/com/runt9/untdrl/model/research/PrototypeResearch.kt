package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.UnitTexture

val advBallistics = research("Advanced Ballistics", UnitTexture.ENEMY, 100) {
    +"All projectiles do 25% more damage and penetrate 10% of enemy resistances."
}

val energyBallistics = research("Energy Ballistics", UnitTexture.ENEMY, 500) {
    dependsOn(advBallistics)
    +"All projectiles do an additional 25% of base damage as Lightning damage and have a 10% chance to stun all enemies hit."
}

val allResearch = listOf(advBallistics, energyBallistics)
