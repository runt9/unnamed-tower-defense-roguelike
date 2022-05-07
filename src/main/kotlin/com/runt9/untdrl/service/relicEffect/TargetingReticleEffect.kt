package com.runt9.untdrl.service.relicEffect

import com.runt9.untdrl.model.loot.definition.TargetingReticleDefinition
import com.runt9.untdrl.service.duringRun.EnemyService

class TargetingReticleEffect(
    private val definition: TargetingReticleDefinition,
    private val enemyService: EnemyService
) : RelicEffect {
    override fun apply() {
        enemyService.globalDamageMultipliers += definition.damageMultiplier
    }
}
