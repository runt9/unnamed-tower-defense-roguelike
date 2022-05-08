package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.enemy.status.DamagingStatusEffect
import com.runt9.untdrl.model.enemy.status.strategy.keepsBetter
import com.runt9.untdrl.model.event.TowerSpecializationSelected
import com.runt9.untdrl.model.faction.EntertainmentNewsDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.MentalDisruptionDefinition
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.proc.DotProc
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.PropagandaTowerAction
import com.runt9.untdrl.service.towerAction.TowerModification
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class EntertainmentNewsEffect(
    override val eventBus: EventBus,
    private val definition: EntertainmentNewsDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    @HandlesEvent
    fun towerSpecialized(event: TowerSpecializationSelected) {
        applyToTower(event.tower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != propagandaTower || !tower.isSpecialization<MentalDisruptionDefinition>()) return

        (tower.action as PropagandaTowerAction).apply {
            modifications += MysticDotModification()
            recalculateModifiers()
        }
    }

    private inner class MysticDotModification : TowerModification {
        private var proc = calculateProc(0f)
        private var newProc = calculateProc(0f)

        private fun calculateProc(buffEffect: Float): DotProc<MysticDot> {
            val hitDmgPct = definition.hitDamagePct * (1f + buffEffect)
            return DotProc(1f, definition.duration, pctOfHitDamage = hitDmgPct) { t, _, td -> MysticDot(t, td) }
        }

        override fun recalculate(buffEffect: Float) {
            newProc = calculateProc(buffEffect)
        }

        override fun applyToTower(tower: Tower) {
            tower.addProc(newProc)
        }

        override fun removeFromTower(tower: Tower) {
            tower.removeProc(proc)
        }

        override fun finish(affectedTowers: Collection<Tower>) {
            proc = newProc
        }
    }

    private inner class MysticDot(source: Tower, damage: Float) : DamagingStatusEffect<MysticDot>(source, definition.duration, damage, DamageType.MYSTIC, DamageSource.OTHER_DOT, keepsBetter())
}
