package com.runt9.untdrl.util.framework.ui.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.runt9.untdrl.util.framework.ui.controller.Controller
import ktx.app.KtxInputAdapter

abstract class GameScreen(worldWidth: Float, worldHeight: Float) : UiScreen() {
    private val camera = OrthographicCamera(worldWidth, worldHeight)
    private val gameStage: UnTdRlStage = UnTdRlStage(FitViewport(worldWidth, worldHeight, camera))
    private val cameraController = GameCameraController(camera)
    override val stages = listOf(gameStage, uiStage)
    abstract val gameController: Controller

    override fun show() {
        input.addProcessor(gameStage)
        input.addProcessor(cameraController)
        gameController.load()
        gameStage.setView(gameController.view)
        super.show()
    }

    override fun hide() {
        super.hide()
        input.removeProcessor(gameStage)
        gameController.dispose()
    }

    override fun render(delta: Float) {
        cameraController.update(delta)
        super.render(delta)
    }
}

class GameCameraController(private val camera: OrthographicCamera) : KtxInputAdapter {
    var forwardKeys = listOf(Input.Keys.W, Input.Keys.UP)
    var backwardKeys = listOf(Input.Keys.S, Input.Keys.DOWN)
    var rightKeys = listOf(Input.Keys.D, Input.Keys.RIGHT)
    var leftKeys = listOf(Input.Keys.A, Input.Keys.LEFT)
    var translateButton = Input.Buttons.RIGHT

    var forwardPressed = false
    var backwardPressed = false
    var rightPressed = false
    var leftPressed = false
    var touched = false
    var button = -1
    var startX = 0f
    var startY = 0f

    var translateUnits = 5f
    var scrollFactor = 0.1f

    fun update(delta: Float) {
        if (leftPressed || rightPressed || forwardPressed || backwardPressed) {
            if (forwardPressed) camera.translate(Vector2(0f, 1f).scl(delta * translateUnits))
            if (backwardPressed) camera.translate(Vector2(0f, -1f).scl(delta * translateUnits))
            if (leftPressed) camera.translate(Vector2(-1f, 0f).scl(delta * translateUnits))
            if (rightPressed) camera.translate(Vector2(1f, 0f).scl(delta * translateUnits))
            camera.update()
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touched = true
        if (this.button < 0) {
            startX = screenX.toFloat()
            startY = screenY.toFloat()
            this.button = button
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touched = false
        if (this.button == button) this.button = -1
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val result = super.touchDragged(screenX, screenY, pointer)
        if (result || button < 0) return result
        val deltaX = (screenX - startX) / Gdx.graphics.width
        val deltaY = (startY - screenY) / Gdx.graphics.height
        startX = screenX.toFloat()
        startY = screenY.toFloat()
        return process(deltaX, deltaY, button)
    }

    private fun process(deltaX: Float, deltaY: Float, button: Int): Boolean {
        if (button == translateButton) {
            camera.translate(Vector2(-deltaX, -deltaY).scl(translateUnits))
            camera.update()
        }
        return true
    }


    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return zoom(amountY * scrollFactor)
    }

    fun zoom(amount: Float): Boolean {
        if ((camera.zoom <= 0.5f && amount < 0) || camera.zoom >= 3f && amount > 0) return false

        camera.zoom += amount
        camera.update()
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when {
            forwardKeys.contains(keycode) -> forwardPressed = true
            backwardKeys.contains(keycode) -> backwardPressed = true
            leftKeys.contains(keycode) -> leftPressed = true
            rightKeys.contains(keycode) -> rightPressed = true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when {
            forwardKeys.contains(keycode) -> forwardPressed = false
            backwardKeys.contains(keycode) -> backwardPressed = false
            leftKeys.contains(keycode) -> leftPressed = false
            rightKeys.contains(keycode) -> rightPressed = false
        }
        return false
    }
}
