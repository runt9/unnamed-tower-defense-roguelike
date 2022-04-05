package com.runt9.untdrl.view.settings

import com.runt9.untdrl.model.config.PlayerSettings
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class SettingsDialogViewModel(settings: PlayerSettings) : ViewModel() {
    val fullscreen = Binding(settings.fullscreen)
    val vsync = Binding(settings.vsync)
}
