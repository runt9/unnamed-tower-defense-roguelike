package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.enemy.status.Bleed
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.SaltInTheWoundDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.intercept.onCrit
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class SaltInTheWoundEffect(
    override val eventBus: EventBus,
    private val definition: SaltInTheWoundDefinition,
    private val towerService: TowerService,
    private val enemyService: EnemyService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rifleTower) return

        tower.addInterceptor(onCrit { _, onCrit ->
            val enemy = onCrit.enemy
            val highestBleed = enemy.statusEffects.filterIsInstance<Bleed>().maxByOrNull { it.remainingDamage } ?: return@onCrit
            enemy.statusEffects.remove(highestBleed)
            val damage = highestBleed.remainingDamage * (1 + definition.damageMultiplier)
            enemyService.performTickDamageSync(DamageSource.BLEED, tower, enemy, damage, listOf(DamageMap(DamageType.PHYSICAL)), false)
        })
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
