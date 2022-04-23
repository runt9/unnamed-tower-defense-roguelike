package com.runt9.untdrl.view.factionSelect

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.enqueueChangeScreen
import com.runt9.untdrl.model.faction.baseFaction
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.randomString
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.UiScreenController
import com.runt9.untdrl.view.duringRun.DuringRunScreen
import com.runt9.untdrl.view.mainMenu.MainMenuScreenController
import kotlin.random.Random

class FactionSelectController(
    private val eventBus: EventBus,
    private val runStateService: RunStateService
) : UiScreenController() {
    override val vm = FactionSelectViewModel()
    override val view = FactionSelectView(this, vm)

    override fun load() {
        vm.factionOptions = listOf(baseFaction)
    }

    fun back() = eventBus.enqueueChangeScreen<MainMenuScreenController>()

    fun startRun() = launchOnRenderingThread {
        val faction = vm.selectedFaction
        val seed = vm.seed.get().ifBlank { Random.randomString(8) }
        runStateService.save(RunState(seed = seed, faction = faction))
        eventBus.enqueueChangeScreen<DuringRunScreen>()
    }
}
