package com.runt9.untdrl

import com.badlogic.gdx.Application
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.runt9.untdrl.config.ApplicationInitializer
import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.model.event.ChangeScreenRequest
import com.runt9.untdrl.model.event.ExitRequest
import com.runt9.untdrl.util.ext.inject
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.core.UnTdRlScreen
import com.runt9.untdrl.view.duringRun.DuringRunScreen
import com.runt9.untdrl.view.factionSelect.FactionSelectController
import com.runt9.untdrl.view.loading.LoadingScreenController
import com.runt9.untdrl.view.mainMenu.MainMenuScreenController
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.onRenderingThread

class UnTdRlGame : KtxGame<KtxScreen>() {
    private val logger = unTdRlLogger()
    private val initializer by lazyInject<ApplicationInitializer>()
    private val input by lazyInject<Input>()
    private val eventBus by lazyInject<EventBus>()
    private val app by lazyInject<Application>()

    override fun create() {
        initializer.initialize()

        Injector.initGdxDeps()
        Injector.initRunningDeps()

        input.inputProcessor = inject<InputMultiplexer>()
        eventBus.registerHandlers(this)

        addScreen<LoadingScreenController>()
        addScreen<MainMenuScreenController>()
        addScreen<FactionSelectController>()
        addScreen<DuringRunScreen>()
        setScreen<LoadingScreenController>()
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
        initializer.shutdown()
    }

    @HandlesEvent
    suspend fun changeScreen(event: ChangeScreenRequest<*>) = onRenderingThread {
        logger.debug { "Changing screen to ${event.screenClass.simpleName}" }
        setScreen(event.screenClass.java)
    }

    @HandlesEvent
    @Suppress("UnusedPrivateMember")
    fun handleExit(event: ExitRequest) {
        app.exit()
    }

    private inline fun <reified S : UnTdRlScreen> addScreen() = addScreen(inject<S>())
}
