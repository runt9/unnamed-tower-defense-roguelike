package com.runt9.untdrl.util.framework.ui.core

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.util.framework.ui.controller.Controller

abstract class GameScreen(worldWidth: Float, worldHeight: Float) : UiScreen() {
    private val camera = OrthographicCamera(worldWidth, worldHeight)
    private val gameStage: UnTdRlStage = UnTdRlStage(FitViewport(worldWidth, worldHeight, camera))
    private val cameraController = GameCameraController(camera)
    override val stages = listOf(gameStage, uiStage)
    abstract val gameController: Controller

    override fun show() {
        Injector.bindSingleton(camera)
        input.addProcessor(gameStage)
        input.addProcessor(cameraController)
        gameController.load()
        gameStage.setView(gameController.view)
        super.show()
    }

    override fun hide() {
        super.hide()
        input.removeProcessor(gameStage)
        input.removeProcessor(cameraController)
        Injector.remove<OrthographicCamera>()
        gameController.dispose()
    }

    override fun render(delta: Float) {
        cameraController.update(delta)
        super.render(delta)
    }
}
