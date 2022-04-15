package com.runt9.untdrl.view.duringRun

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.model.event.CancelOpenItemsEvent
import com.runt9.untdrl.model.event.BuildingSelectedEvent
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.util.framework.event.EventBus
import ktx.app.KtxInputAdapter
import kotlin.math.roundToInt

class DuringRunInputController(private val eventBus: EventBus, private val buildingService: BuildingService) : KtxInputAdapter {
    private val selectButton = Input.Buttons.LEFT
    private val cancelButton = Input.Buttons.RIGHT
    private val cancelKey = Input.Keys.ESCAPE
    private val camera by lazyInject<OrthographicCamera>()

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == cancelButton) {
            eventBus.enqueueEventSync(CancelOpenItemsEvent())
            return false
        }

        if (button == selectButton) {
            val clickPoint = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            buildingService.getBuildingAtPoint(Vector2(clickPoint.x.roundToInt().toFloat(), clickPoint.y.roundToInt().toFloat()))?.apply {
                eventBus.enqueueEventSync(BuildingSelectedEvent(this))
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
