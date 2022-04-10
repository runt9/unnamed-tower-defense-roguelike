package com.runt9.untdrl.view.duringRun.game.tower

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.runt9.untdrl.config.lazyInject
import com.runt9.untdrl.model.Tower
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.app.KtxInputAdapter
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import kotlin.math.roundToInt

@Scene2dDsl
fun <S> KWidget<S>.tower(tower: TowerViewModel, init: TowerView.(S) -> Unit = {}) = uiComponent<S, TowerController, TowerView>({
    this.vm = tower
    this.initTowerMover()
}, init)

class TowerController(private val eventBus: EventBus) : Controller {
    override lateinit var vm: TowerViewModel
    override val view by lazy { TowerView(this, vm) }
    private val input by lazyInject<InputMultiplexer>()
    private val camera by lazyInject<OrthographicCamera>()

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun initTowerMover() {
        if (!vm.isPlaced.get()) {
            input.addProcessor(TowerInputMover(vm.tower, camera, {
                vm.position(position.cpy())
            }) {
                vm.isPlaced(true)
                input.removeProcessor(this)
                eventBus.enqueueEventSync(TowerPlacedEvent(vm.tower))
            })
        }
    }
}

class TowerInputMover(private val tower: Tower, private val camera: OrthographicCamera, private val towerMoved: Tower.() -> Unit, private val onComplete: TowerInputMover.() -> Unit) :
    KtxInputAdapter {
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val cameraVector = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val worldX = cameraVector.x.roundToInt()
        val worldY = cameraVector.y.roundToInt()

        if (worldX != tower.position.x.roundToInt() || worldY != tower.position.y.roundToInt()) {
            tower.position.set(Vector2(worldX.toFloat(), worldY.toFloat()))
            tower.towerMoved()
            return false
        }

        return super.mouseMoved(screenX, screenY)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // TODO: Validate
        if (button == Input.Buttons.LEFT) {
            onComplete(this)
            return false
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }
}
