package com.runt9.untdrl.service.asset

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.config.AssetConfig
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.event.AssetsLoadedEvent
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

class AssetLoader(
    private val assets: AssetStorage,
    private val eventBus: EventBus,
    private val assetConfig: AssetConfig
) : Disposable {
    private val logger = unTdRlLogger()

    fun load() = KtxAsync.launch(assetConfig.asyncContext) {
        logger.info { "Loading assets" }
        val assetsToLoad = UnitTexture.values().map { assets.loadAsync<Texture>(it.assetFile) }
        assetsToLoad.joinAll()
        logger.info { "Asset loading complete" }
        eventBus.enqueueEvent(AssetsLoadedEvent())
    }

    override fun dispose() {
        logger.info { "Disposing" }
        assetConfig.dispose()
    }
}
