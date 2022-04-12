package com.runt9.untdrl.util.framework.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.runt9.untdrl.util.framework.event.EventBus
import ktx.app.KtxInputAdapter
import kotlin.math.roundToInt

open class InputMover<T : Steerable<Vector2>>(
    private val item: T,
    private val camera: OrthographicCamera,
    private val eventBus: EventBus,
    private val onMove: T.() -> Unit,
    private val onClick: InputMover<T>.() -> Boolean,
    private val onCancel: InputMover<T>.() -> Unit
) : KtxInputAdapter {
    private val primaryClickButton = Input.Buttons.LEFT;
    private val cancelButton = Input.Buttons.RIGHT;
    private val cancelKey = Input.Keys.ESCAPE

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val cameraVector = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val worldX = cameraVector.x.roundToInt()
        val worldY = cameraVector.y.roundToInt()

        if (worldX != item.position.x.roundToInt() || worldY != item.position.y.roundToInt()) {
            item.position.set(Vector2(worldX.toFloat(), worldY.toFloat()))
            item.onMove()
        }

        return super.mouseMoved(screenX, screenY)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == primaryClickButton) {
            return onClick(this)
        }

        if (button == cancelButton) {
            onCancel()
            return false
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == cancelKey) {
            onCancel()
            return false
        }

        return super.keyUp(keycode)
    }
}
