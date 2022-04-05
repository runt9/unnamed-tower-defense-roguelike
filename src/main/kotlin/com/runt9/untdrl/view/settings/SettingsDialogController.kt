package com.runt9.untdrl.view.settings

import com.badlogic.gdx.Graphics
import com.runt9.untdrl.config.PlayerSettingsConfig
import com.runt9.untdrl.model.config.PlayerSettings
import com.runt9.untdrl.util.framework.ui.controller.DialogController

class SettingsDialogController(private val settingsConfig: PlayerSettingsConfig, graphics: Graphics) : DialogController() {
    private var currentSettings = settingsConfig.get()
    override val vm = SettingsDialogViewModel(currentSettings)
    override val view = SettingsDialogView(this, vm, graphics.width, graphics.height)

    fun applySettings() {
        val newSettings = PlayerSettings(
            fullscreen = vm.fullscreen.get(),
            vsync = vm.vsync.get(),
            // NB: Not going to be in the dialog
            logLevel = currentSettings.logLevel,
            // TODO: Add resolution selection to dialog
            resolution = currentSettings.resolution
        )

        settingsConfig.apply(newSettings)
        // TODO: Confirm settings work before saving
        settingsConfig.save(newSettings)
        currentSettings = newSettings
        vm.saveCurrent()
    }
}
