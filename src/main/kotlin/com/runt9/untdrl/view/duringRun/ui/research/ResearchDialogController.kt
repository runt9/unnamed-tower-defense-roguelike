package com.runt9.untdrl.view.duringRun.ui.research

import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.research.ResearchDefinition
import com.runt9.untdrl.service.duringRun.ResearchService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.DialogController
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread

class ResearchDialogController(
    graphics: Graphics,
    private val runStateService: RunStateService,
    private val eventBus: EventBus,
    private val researchService: ResearchService,
    private val assets: AssetStorage
) : DialogController() {
    override val vm = ResearchDialogViewModel()
    override val view = ResearchDialogView(this, vm, graphics.width, graphics.height)

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

    fun applyResearch(research: ResearchDefinition) {
        if (vm.researchAmount.get() < research.cost) return
        researchService.applyResearch(research)
    }

    fun loadTexture(icon: UnitTexture): Texture = assets[icon.assetFile]
}
