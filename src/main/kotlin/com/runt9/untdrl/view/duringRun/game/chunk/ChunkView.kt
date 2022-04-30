package com.runt9.untdrl.view.duringRun.game.chunk

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.GroupView
import com.runt9.untdrl.view.duringRun.CHUNK_SIZE
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.scene2d.table
import ktx.scene2d.vis.visTable

class ChunkView(override val controller: ChunkController, override val vm: ChunkViewModel) : GroupView() {
    private val logger = unTdRlLogger()

    override fun init() {
        val vm = vm

        setSize(CHUNK_SIZE.toFloat(), CHUNK_SIZE.toFloat())
        setBounds(0f, 0f, CHUNK_SIZE.toFloat(), CHUNK_SIZE.toFloat())
        setOrigin(Align.center)

        bindUpdatable(vm.isValidPlacement) {
            alpha = if (vm.isValidPlacement.get()) 1f else 0.5f
        }

        visTable {
            vm.chunk.grid.reversedArray().forEach { row ->
                row.forEach { col ->
                    val color = when (col) {
                        1 -> Color.WHITE
                        2 -> Color.GREEN
                        3 -> vm.spawnerColor.get()
                        else -> Color.DARK_GRAY
                    }

                    table {
                        background(rectPixmapTexture(1, 1, color).toDrawable())
                    }.cell(grow = true)
                }

                row()
            }

            setSize(CHUNK_SIZE.toFloat(), CHUNK_SIZE.toFloat())
            setOrigin(Align.center)
        }

        bindUpdatable(vm.position) { vm.position.get().apply { setPosition(x, y, Align.center) } }
        bindUpdatable(vm.rotation) {
            vm.rotation.get().apply {
                logger.info { "Rotating chunk from $rotation to $this" }
                val rotateAction = rotateTo(this, 0.25f)
                rotateAction.isUseShortestDirection = true
                this@bindUpdatable += rotateAction
            }
        }
    }
}
