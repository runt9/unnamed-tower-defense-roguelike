package com.runt9.untdrl.view.duringRun.game.chunk

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.InputMover

class ChunkInputMover(
    private val chunk: Chunk,
    camera: OrthographicCamera,
    eventBus: EventBus,
    private val chunkMoved: Chunk.() -> Unit,
    onClick: InputMover<Chunk>.() -> Boolean,
    onCancel: InputMover<Chunk>.() -> Unit
) : InputMover<Chunk>(chunk, camera, eventBus, chunkMoved, onClick, onCancel) {
    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.Q -> {
                chunk.rotate(false)
                chunk.chunkMoved()
                return false
            }
            Input.Keys.E -> {
                chunk.rotate(true)
                chunk.chunkMoved()
                return false
            }
        }

        return super.keyUp(keycode)
    }
}
