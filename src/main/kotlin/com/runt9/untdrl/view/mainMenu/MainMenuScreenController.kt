package com.runt9.untdrl.view.mainMenu

import com.runt9.untdrl.model.event.enqueueChangeScreen
import com.runt9.untdrl.model.event.enqueueExitRequest
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.UiScreenController
import com.runt9.untdrl.util.framework.ui.viewModel.emptyViewModel
import com.runt9.untdrl.view.settings.SettingsDialogController

class MainMenuScreenController(private val eventBus: EventBus) : UiScreenController() {
    override val vm = emptyViewModel()
    override val view = MainMenuView(this, vm)

    fun newRun(): Any = TODO()//eventBus.enqueueChangeScreen<HeroSelectScreenController>()
    fun showSettings() = eventBus.enqueueShowDialog<SettingsDialogController>()
    fun exit() = eventBus.enqueueExitRequest()
}
