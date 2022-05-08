package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.enemy.status.Stun
import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.faction.DefectorsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.SayItLouderDefinition
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.specializationEffect.SayItLouderEffect
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class DefectorsEffect(
    override val eventBus: EventBus,
    private val definition: DefectorsDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    private val affectedEnemies = mutableSetOf<Enemy>()

    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        applyToTower(event.tower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != propagandaTower || !tower.isSpecialization<SayItLouderDefinition>()) return

        (tower.appliedSpecializationEffect as SayItLouderEffect).addCheck { _, enemy ->
            if (affectedEnemies.contains(enemy)) return@addCheck

            enemy.addStatusEffect(Stun(tower, definition.stunDuration))
            affectedEnemies += enemy
        }
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        affectedEnemies.clear()
    }
}
