package com.runt9.untdrl.model

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.path.GridNode
import com.runt9.untdrl.util.ext.radDeg
import com.runt9.untdrl.util.ext.toAngle
import ktx.collections.toGdxArray

class Spawner(val node: GridNode) {
    var currentPath = DefaultGraphPath<GridNode>()

    fun spawnEnemy(texture: Texture) =
        Enemy(texture, node.point, currentPath.nodes.first().point.sub(node.point).toAngle().radDeg, currentPath.nodes.map(GridNode::point).toGdxArray())
}
