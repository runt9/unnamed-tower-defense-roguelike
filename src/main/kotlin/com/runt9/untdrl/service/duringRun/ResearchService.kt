package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.research.ResearchDefinition
import com.runt9.untdrl.model.research.ResearchEffectDefinition
import com.runt9.untdrl.model.research.allResearch
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.dynamicInjectCheckIsSubclassOf
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.view.duringRun.REROLL_COST

class ResearchService(
    eventBus: EventBus,
    registry: RunServiceRegistry,
    private val runStateService: RunStateService,
    private val randomizer: RandomizerService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val research = allResearch

    override fun startInternal() {
        runStateService.update { addResearch() }
    }

    fun applyResearch(research: ResearchDefinition) {
        logger.info { "Applying ${research.name}" }

        val effect = dynamicInject(
            research.effect.effectClass,
            dynamicInjectCheckIsSubclassOf(ResearchEffectDefinition::class.java) to research.effect
        )
        effect.init()
        effect.apply()

        runStateService.update {
            researchAmount -= research.cost
            availableResearch -= research
            appliedResearch += research
            selectableResearch -= research

            addResearch()
        }
    }

    fun rerollResearch() {
        runStateService.update {
            gold -= researchRerollCost
            researchRerollCost += REROLL_COST
            selectableResearch = emptyList()
            addResearch()
        }
    }

    private fun RunState.addResearch() {
        val newResearchs = research.filter { up ->
            // Exclude research already made available, already applied, or anything made exclusive
            if (availableResearch.contains(up)) return@filter false
            if (appliedResearch.contains(up)) return@filter false

            // Only include researches with no dependencies or satisfied dependencies
            return@filter appliedResearch.containsAll(up.dependsOn)
        }

        availableResearch += newResearchs
        while (selectableResearch.size < selectableResearchOptionCount && selectableResearch.size < availableResearch.size) {
            selectableResearch += availableResearch.filter { !selectableResearch.contains(it) }.random(randomizer.rng)
        }
    }
}
