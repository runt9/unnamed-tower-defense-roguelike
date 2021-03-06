package com.runt9.untdrl.view.duringRun.ui.runEnd

import com.runt9.untdrl.model.event.enqueueChangeScreen
import com.runt9.untdrl.model.event.enqueueExitRequest
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.DialogController
import com.runt9.untdrl.util.framework.ui.controller.injectView
import com.runt9.untdrl.view.mainMenu.MainMenuScreenController

class RunEndDialogController(private val eventBus: EventBus, private val runStateService: RunStateService) : DialogController() {
    override val vm = RunEndDialogViewModel()
    override val view = injectView<RunEndDialogView>()

    override fun load() {
        runStateService.load().apply { vm.runWon(hp > 0) }
    }

    fun mainMenu() {
        eventBus.enqueueChangeScreen<MainMenuScreenController>()
        hide()
    }
    fun exit() = eventBus.enqueueExitRequest()
}
