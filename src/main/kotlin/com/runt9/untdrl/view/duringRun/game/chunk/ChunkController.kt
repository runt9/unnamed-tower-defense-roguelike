package com.runt9.untdrl.view.duringRun.game.chunk

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.runt9.untdrl.util.ext.lazyInject
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.service.duringRun.IndexedGridGraph
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

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
            input.addProcessor(ChunkInputMover(vm.chunk, camera, eventBus, {
                vm.isValidPlacement(this@ChunkController.grid.isValidChunkPlacement(vm.chunk))
                vm.position(position.cpy())
                vm.rotation(rotation)
            }, {
                if (!grid.isValidChunkPlacement(vm.chunk)) {
                    return@ChunkInputMover false
                }

                vm.isPlaced(true)
                input.removeProcessor(this)
                eventBus.enqueueEventSync(ChunkPlacedEvent(vm.chunk))
                return@ChunkInputMover true
            }) {
                input.removeProcessor(this)
                eventBus.enqueueEventSync(ChunkCancelledEvent(vm.chunk))
            })
        }
    }
}

