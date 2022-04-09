package com.runt9.untdrl.model.path

import com.badlogic.gdx.math.Vector2

enum class GridNodeType {
    EMPTY, PATH, HOME, SPAWNER
}

class GridNode(val x: Float, val y: Float, val index: Int, val type: GridNodeType = GridNodeType.EMPTY) {
    val point: Vector2 get() = Vector2(x, y)
}
