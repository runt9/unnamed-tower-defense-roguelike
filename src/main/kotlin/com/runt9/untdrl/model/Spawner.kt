package com.runt9.untdrl.model

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.path.GridNode
import ktx.collections.toGdxArray

class Spawner(val node: GridNode) {
    var currentPath = DefaultGraphPath<GridNode>()

    fun spawnEnemy(texture: Texture) =
        Enemy(texture, node.point, node.point.angleDeg(currentPath.nodes.first().point), currentPath.nodes.map(GridNode::point).toGdxArray())
}
