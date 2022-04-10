package com.runt9.untdrl.view.duringRun.game.chunk

import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.runt9.untdrl.config.lazyInject
import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.path.IndexedGridGraph
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

class ChunkController(private val eventBus: EventBus, private val grid: IndexedGridGraph) : Controller {
    override lateinit var vm: ChunkViewModel
    override val view by lazy { ChunkView(this, vm) }
    private val input by lazyInject<InputMultiplexer>()
    private val camera by lazyInject<OrthographicCamera>()

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun initChunkMover() {
        if (!vm.isPlaced.get()) {
            input.addProcessor(ChunkInputMover(vm.chunk, camera, {
                vm.isValidPlacement(this@ChunkController.grid.isValidChunkPlacement(vm.chunk))
                vm.position(position)
                vm.rotation(rotation)
            }) {
                if (!grid.isValidChunkPlacement(vm.chunk)) {
                    return@ChunkInputMover false
                }

                vm.isPlaced(true)
                input.removeProcessor(this)
                eventBus.enqueueEventSync(ChunkPlacedEvent(vm.chunk))
                return@ChunkInputMover true
            })
        }
    }
}

class ChunkInputMover(private val chunk: Chunk, private val camera: OrthographicCamera, private val chunkMoved: Chunk.() -> Unit, private val onClick: ChunkInputMover.() -> Boolean) : KtxInputAdapter {
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val cameraVector = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val worldX = cameraVector.x.roundToInt()
        val worldY = cameraVector.y.roundToInt()

        if (worldX != chunk.position.x.roundToInt() || worldY != chunk.position.y.roundToInt()) {
            chunk.position = Vector2(worldX.toFloat(), worldY.toFloat())
            chunk.chunkMoved()
            return false
        }

        return super.mouseMoved(screenX, screenY)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if (button == Buttons.LEFT) onClick(this) else super.touchDown(screenX, screenY, pointer, button)

    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.Q -> chunk.rotate(false)
            Input.Keys.E -> chunk.rotate(true)
        }
        chunk.chunkMoved()
        return false
    }
}
