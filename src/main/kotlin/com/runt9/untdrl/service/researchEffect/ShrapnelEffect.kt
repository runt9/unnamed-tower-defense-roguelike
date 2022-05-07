package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.enemy.status.Bleed
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.ShrapnelDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.ResistanceRequest
import com.runt9.untdrl.model.tower.proc.TowerProc
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ShrapnelEffect(
    override val eventBus: EventBus,
    private val definition: ShrapnelDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    var duration = definition.duration

    private val shrapnelBleedProc = object : TowerProc {
        override val chance: Float = definition.bleedChance

        override fun applyToEnemy(tower: Tower, enemy: Enemy, resistRequest: ResistanceRequest) {
            val totalDamage = resistRequest.getDamageForType(DamageType.PHYSICAL) * definition.pctOfPhysicalDamage
            if (totalDamage > 0) {
                enemy.addStatusEffect(Bleed(tower, duration, totalDamage))
            }
        }
    }

    override fun init() {
        super.init()
        Injector.bindSingleton(this)
    }

    override fun apply() {
        towerService.forEachTower { it.addProc(shrapnelBleedProc) }
    }

    override fun dispose() {
        super.dispose()
        Injector.removeProvider(ShrapnelEffect::class.java)
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        event.tower.addProc(shrapnelBleedProc)
    }
}
