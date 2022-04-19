package com.runt9.untdrl.model.research

import com.runt9.untdrl.model.UnitTexture

val ironWorking = research("Iron Working", UnitTexture.ENEMY, 100)

val steelWorking = research("Steel Working", UnitTexture.ENEMY, 500) {
    dependsOn(ironWorking)
}

val allResearch = listOf(ironWorking, steelWorking)
