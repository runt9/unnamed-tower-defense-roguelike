package com.runt9.untdrl.config

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.runt9.untdrl.model.config.PlayerSettings
import com.runt9.untdrl.util.ext.getMatching


class ApplicationConfiguration(settingsConfig: PlayerSettingsConfig) : Lwjgl3ApplicationConfiguration() {
    init {
        val settings = settingsConfig.get()
        setTitle("Untitled Building Defense Roguelike")
        handleResolution(settings.fullscreen, settings.resolution)
        useVsync(settings.vsync)
        setResizable(false)
    }

    private fun handleResolution(fullscreen: Boolean, resolution: PlayerSettings.Resolution) {
        if (fullscreen) {
            setFullscreenMode(getDisplayModes().getMatching(resolution, getDisplayMode()))
        } else {
            resolution.apply { setWindowedMode(width, height) }
        }
    }
}
