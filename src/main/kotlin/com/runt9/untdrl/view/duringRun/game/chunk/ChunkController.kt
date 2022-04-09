package com.runt9.untdrl.view.duringRun.game.chunk

import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.runt9.untdrl.config.lazyInject
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.app.KtxInputAdapter
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import kotlin.math.roundToInt

@Scene2dDsl
fun <S> KWidget<S>.chunk(chunk: ChunkViewModel, init: ChunkView.(S) -> Unit = {}) = uiComponent<S, ChunkController, ChunkView>({
    this.vm = chunk
    this.initChunkMover()
}, init)

class ChunkController(private val eventBus: EventBus) : Controller {
    override lateinit var vm: ChunkViewModel
    override val view by lazy { ChunkView(this, vm) }
    val input by lazyInject<InputMultiplexer>()
    val camera by lazyInject<OrthographicCamera>()

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun initChunkMover() {
        if (!vm.isPlaced.get()) {
            input.addProcessor(ChunkInputMover(vm, camera) { input.removeProcessor(this) })
        }
    }
}

class ChunkInputMover(private val chunk: ChunkViewModel, private val camera: OrthographicCamera, private val onComplete: ChunkInputMover.() -> Unit) : KtxInputAdapter {
    private val logger = unTdRlLogger()

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val cameraVector = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val worldX = cameraVector.x.roundToInt()
        val worldY = cameraVector.y.roundToInt()

        if (worldX != chunk.position.get().x.roundToInt() || worldY != chunk.position.get().y.roundToInt()) {
            logger.info { "Mouse moved to $worldX, $worldY" }
            chunk.position(Vector2(worldX.toFloat(), worldY.toFloat()))
            return false
        }

        return super.mouseMoved(screenX, screenY)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // TODO: Validate
        if (button == Buttons.LEFT) {
            chunk.isPlaced(true)
            onComplete(this)
            return false
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.Q -> chunk.rotation(chunk.rotation.get() - 90f)
            Input.Keys.E -> chunk.rotation(chunk.rotation.get() + 90f)
        }
        return false
    }
}
