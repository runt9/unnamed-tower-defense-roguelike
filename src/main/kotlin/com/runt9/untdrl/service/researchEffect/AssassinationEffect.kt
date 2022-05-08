package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.faction.AssassinationDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.SniperSpecialization
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.model.tower.intercept.critCheck
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class AssassinationEffect(
    override val eventBus: EventBus,
    private val definition: AssassinationDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rifleTower || !tower.isSpecialization<SniperSpecialization>()) return

        tower.addInterceptor(critCheck { _, check ->
            val enemy = check.enemy
            val enemyMissingHp = (1 - (enemy.currentHp / enemy.maxHp)) * 100f
            val critIncrease = enemyMissingHp * definition.critPerMissingHp
            check.addCritChanceIncrease(critIncrease)
        })
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        applyToTower(event.tower)
    }
}
