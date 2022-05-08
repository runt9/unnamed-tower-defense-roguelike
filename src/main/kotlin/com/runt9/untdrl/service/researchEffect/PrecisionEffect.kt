package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.faction.PrecisionDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.rifleTower
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.intercept.beforeDamage
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class PrecisionEffect(
    override val eventBus: EventBus,
    private val definition: PrecisionDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    private val stacks = mutableMapOf<Tower, MutableList<AttributeModifier>>()

    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != rifleTower) return

        stacks[tower] = mutableListOf()

        tower.addInterceptor(beforeDamage(DamageSource.PROJECTILE) { _, dr ->
            val myStacks = stacks[tower]!!

            if (dr.wasCrit) {
                tower.attrMods -= myStacks.toSet()
                myStacks.clear()
            } else {
                val stack = AttributeModifier(AttributeType.CRIT_CHANCE, flatModifier = definition.critBonus, isTemporary = true)
                tower.attrMods += stack
                myStacks += stack
            }

            towerService.recalculateAttrsSync(tower)
        })
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        stacks.values.forEach { it.clear() }
    }
}
