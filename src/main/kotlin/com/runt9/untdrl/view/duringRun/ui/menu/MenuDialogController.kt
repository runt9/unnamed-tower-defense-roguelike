package com.runt9.untdrl.view.duringRun.ui.menu

import com.badlogic.gdx.Graphics
import com.runt9.untdrl.model.event.enqueueChangeScreen
import com.runt9.untdrl.model.event.enqueueExitRequest
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.model.event.pauseGame
import com.runt9.untdrl.model.event.resumeGame
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.DialogController
import com.runt9.untdrl.view.mainMenu.MainMenuScreenController
import com.runt9.untdrl.view.settings.SettingsDialogController

class MenuDialogController(private val eventBus: EventBus, graphics: Graphics, private val runStateService: RunStateService) : DialogController() {
    override val vm = MenuDialogViewModel()
    override val view = MenuDialogView(this, vm, graphics.width, graphics.height)

    override fun load() {
        runStateService.load().apply { vm.runSeed(seed) }
        eventBus.pauseGame()
    }

    fun resume() {
        eventBus.resumeGame()
        hide()
    }
    fun settings() = eventBus.enqueueShowDialog<SettingsDialogController>()
    fun mainMenu() {
        eventBus.enqueueChangeScreen<MainMenuScreenController>()
        hide()
    }
    fun exit() = eventBus.enqueueExitRequest()
}
