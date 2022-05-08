package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.faction.PowerInNumbersDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class PowerInNumbersEffect(
    override val eventBus: EventBus,
    private val definition: PowerInNumbersDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    private val mods = mutableMapOf<Tower, AttributeModifier>()

    override fun apply() {
        towerService.forEachTower(::applyToTower)
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        if (event.tower.definition == propagandaTower) {
            applyToTower(event.tower)
        } else {
            applyToPropagandaTowersInRange(event.tower)
        }
    }

    private fun applyToPropagandaTowersInRange(tower: Tower) {
        towerService.allTowers.filter { it.definition == propagandaTower && it.inRangeOf(tower.position) }.forEach(::applyToTower)
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != propagandaTower) return

        towerService.onTowerChange(tower.id) {
            refreshTowerMod(tower)
        }

        refreshTowerMod(tower)
    }

    private fun refreshTowerMod(tower: Tower) {
        val towerCount = towerService.towersInRange(tower).size
        val buffIncrease = towerCount * definition.buffEffectPerTower
        val mod = AttributeModifier(AttributeType.BUFF_DEBUFF_EFFECT, percentModifier = buffIncrease)

        if (mods.containsKey(tower)) {
            val oldMod = mods[tower]!!
            if (oldMod.percentModifier == mod.percentModifier) return
            tower.removeAttributeModifier(oldMod)
        }

        tower.addAttributeModifier(mod)
        mods[tower] = mod
        towerService.recalculateAttrsSync(tower)
    }
}
