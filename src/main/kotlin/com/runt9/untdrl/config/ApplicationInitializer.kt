package com.runt9.untdrl.config

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager
import ktx.async.KtxAsync
import com.runt9.untdrl.service.asset.AssetLoader
import com.runt9.untdrl.service.asset.SkinLoader
import com.runt9.untdrl.util.framework.event.EventBus


class ApplicationInitializer(
    private val eventBus: EventBus,
    private val assetLoader: AssetLoader,
    private val config: PlayerSettingsConfig,
    private val skinLoader: SkinLoader
) {
    fun initialize() {
        Gdx.app.logLevel = config.get().logLevel
        KtxAsync.initiate()

        TooltipManager.getInstance().apply {
            instant()
            animations = false
        }

        skinLoader.initializeSkin()
        eventBus.loop()
        assetLoader.load()
    }

    fun shutdown() {
        eventBus.dispose()
        assetLoader.dispose()
    }
}
