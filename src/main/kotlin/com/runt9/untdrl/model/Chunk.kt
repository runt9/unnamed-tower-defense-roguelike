package com.runt9.untdrl.model

import com.badlogic.gdx.math.MathUtils
import com.runt9.untdrl.util.ext.LocationAdapter
import com.runt9.untdrl.view.duringRun.CHUNK_SIZE

class Chunk(var grid: Array<IntArray>) : LocationAdapter() {
    fun rotate(clockwise: Boolean) {
        val newGrid = Array(CHUNK_SIZE) { IntArray(CHUNK_SIZE) }

        rotation = if (clockwise) MathUtils.lerpAngleDeg(rotation, rotation - 90f, 1f) else MathUtils.lerpAngleDeg(rotation, rotation + 90f, 1f)

        grid.forEachIndexed { x, row ->
            row.forEachIndexed { y, col ->
                val xToSet = if (clockwise) CHUNK_SIZE - 1 - y else y
                val yToSet = if (clockwise) x else CHUNK_SIZE - 1 - x

                newGrid[xToSet][yToSet] = col
            }
        }

        grid = newGrid
    }
}
