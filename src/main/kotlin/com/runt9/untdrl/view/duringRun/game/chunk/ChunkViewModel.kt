package com.runt9.untdrl.view.duringRun.game.chunk

import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class ChunkViewModel(val chunk: Chunk) : ViewModel() {
    val position = Binding(chunk.position.cpy())
    val rotation = Binding(chunk.rotation)
    val isPlaced = Binding(false)
    val isValidPlacement = Binding(false)
}
