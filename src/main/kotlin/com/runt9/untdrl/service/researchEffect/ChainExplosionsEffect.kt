package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.ChainExplosionsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rocketTower
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.intercept.onKill
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.view.duringRun.NEARBY

class ChainExplosionsEffect(
    override val eventBus: EventBus,
    private val definition: ChainExplosionsDefinition,
    private val enemyService: EnemyService,
    private val towerService: TowerService
) : ResearchEffect {
    private val damageTypes = listOf(
        DamageMap(DamageType.PHYSICAL, pctOfBase = 0.5f),
        DamageMap(DamageType.HEAT, pctOfBase = 0.5f)
    )

    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rocketTower) return

        tower.addInterceptor(onKill { _, onKill ->
            val enemy = onKill.enemy

            val damage = enemy.maxHp * definition.lifePct
            enemyService.enemiesInRange(enemy.position, NEARBY).forEach { e ->
                enemyService.performTickDamageSync(DamageSource.TOWER, tower, e, damage, damageTypes, false)
            }
        })
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }
}
