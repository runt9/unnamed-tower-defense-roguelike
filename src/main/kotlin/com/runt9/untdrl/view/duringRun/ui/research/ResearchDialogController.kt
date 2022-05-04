package com.runt9.untdrl.view.duringRun.ui.research

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.research.ResearchItem
import com.runt9.untdrl.service.duringRun.ResearchService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.DialogController
import com.runt9.untdrl.util.framework.ui.controller.injectView
import ktx.async.onRenderingThread

class ResearchDialogController(
    private val runStateService: RunStateService,
    private val eventBus: EventBus,
    private val researchService: ResearchService
) : DialogController() {
    override val vm = ResearchDialogViewModel()
    override val view = injectView<ResearchDialogView>()

    override fun load() {
        eventBus.registerHandlers(this)
        runStateService.load().applyNewState()
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }

    @HandlesEvent
    suspend fun runStateUpdated(event: RunStateUpdated) = onRenderingThread { event.newState.applyNewState() }

    private fun RunState.applyNewState() {
        vm.rerollCost(researchRerollCost)
        vm.gold(gold)
        vm.researchAmount(researchAmount)
        vm.research(selectableResearch)
    }

    fun done() {
        hide()
    }

    fun reroll() {
        researchService.rerollResearch()
    }

    fun applyResearch(research: ResearchItem) {
        if (vm.researchAmount.get() < research.cost) return
        researchService.applyResearch(research)
    }
}
