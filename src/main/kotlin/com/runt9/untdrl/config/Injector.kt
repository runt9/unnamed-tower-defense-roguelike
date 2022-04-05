package com.runt9.untdrl.config

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ai.GdxAI
import ktx.inject.Context
import ktx.inject.register
import com.runt9.untdrl.UnTdRlGame
import com.runt9.untdrl.service.asset.AssetLoader
import com.runt9.untdrl.service.asset.SkinLoader
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.DialogManager
import com.runt9.untdrl.view.loading.LoadingScreenController
import com.runt9.untdrl.view.mainMenu.MainMenuScreenController
import com.runt9.untdrl.view.settings.SettingsDialogController

inline fun <reified Type : Any> inject(): Type = Injector.inject()
inline fun <reified Type : Any> lazyInject() = lazy { inject<Type>() }

object Injector : Context() {
    fun initStartupDeps() = register {
        bindSingleton<UnTdRlGame>()
        bindSingleton<PlayerSettingsConfig>()
        bindSingleton<ApplicationConfiguration>()
        bindSingleton<EventBus>()
        bindSingleton<AssetConfig>()
        bindSingleton<SkinLoader>()
        bindSingleton<AssetLoader>()
        bindSingleton<ApplicationInitializer>()
    }

    fun initGdxDeps() = register {
        bindSingleton(Gdx.app)
        bindSingleton(Gdx.audio)
        bindSingleton(Gdx.files)
        bindSingleton(Gdx.gl)
        bindSingleton(Gdx.graphics)
        bindSingleton(Gdx.input)
        bindSingleton(Gdx.net)
    }

    fun initRunningDeps() = register {
        bindSingleton(InputMultiplexer())
        bindSingleton(GdxAI.getTimepiece())

        bindSingleton<DialogManager>()
        bindSingleton<LoadingScreenController>()
        bindSingleton<MainMenuScreenController>()
        bindSingleton<SettingsDialogController>()
    }
}
