package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.faction.GrowTheCultDefinition
import com.runt9.untdrl.model.tower.definition.propagandaTower
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.factionPassiveEffect.ResearchMultiplier
import com.runt9.untdrl.service.factionPassiveEffect.RndBudgetEffect
import com.runt9.untdrl.util.framework.event.EventBus

class GrowTheCultEffect(
    override val eventBus: EventBus,
    private val definition: GrowTheCultDefinition,
    private val rndBudgetEffect: RndBudgetEffect,
    private val towerService: TowerService
) : ResearchEffect {
    private val modifier: ResearchMultiplier = {
        val propagandaTowerCount = towerService.allTowers.count { it.definition == propagandaTower }
        definition.researchPerTower * propagandaTowerCount
    }

    override fun apply() {
        rndBudgetEffect.addResearchModifier(modifier)
    }

    override fun dispose() {
        super.dispose()
        rndBudgetEffect.removeResearchModifier(modifier)
    }
}
