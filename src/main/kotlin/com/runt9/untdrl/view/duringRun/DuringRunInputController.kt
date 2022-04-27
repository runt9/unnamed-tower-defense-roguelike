package com.runt9.untdrl.view.duringRun

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.runt9.untdrl.model.event.TowerSelectedEvent
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.util.framework.event.EventBus
import ktx.app.KtxInputAdapter
import kotlin.math.roundToInt

class DuringRunInputController(private val eventBus: EventBus, private val towerService: TowerService) : KtxInputAdapter {
    private val selectButton = Input.Buttons.LEFT
    private val cancelButton = Input.Buttons.RIGHT
    private val cancelKey = Input.Keys.ESCAPE
    private val camera by lazyInject<OrthographicCamera>()
    lateinit var stage: Stage

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == cancelButton) {
            eventBus.enqueueEventSync(CancelOpenItemsEvent())
            return false
        }

        if (button == selectButton) {
            val clickPoint = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            towerService.getTowerAtPoint(Vector2(clickPoint.x.roundToInt().toFloat(), clickPoint.y.roundToInt().toFloat()))?.apply {
                eventBus.enqueueEventSync(TowerSelectedEvent(this))
            }
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == cancelKey) {
            eventBus.enqueueEventSync(CancelOpenItemsEvent())
            return false
        }

        return super.keyUp(keycode)
    }
}
