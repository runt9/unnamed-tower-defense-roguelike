package com.runt9.untdrl.util.framework.ui.core

import com.badlogic.gdx.utils.viewport.FitViewport
import com.runt9.untdrl.util.framework.ui.controller.Controller

abstract class GameScreen(worldWidth: Float, worldHeight: Float) : UiScreen() {
    private val gameStage: UnTdRlStage = UnTdRlStage(FitViewport(worldWidth, worldHeight))
    override val stages = listOf(gameStage, uiStage)
    abstract val gameController: Controller

    override fun show() {
        input.addProcessor(gameStage)
        gameController.load()
        gameStage.setView(gameController.view)
        super.show()
    }

    override fun hide() {
        super.hide()
        input.removeProcessor(gameStage)
        gameController.dispose()
    }
}
