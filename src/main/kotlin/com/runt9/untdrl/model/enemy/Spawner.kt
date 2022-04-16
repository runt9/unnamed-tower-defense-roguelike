package com.runt9.untdrl.model.enemy

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.path.GridNode
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.radDeg
import com.runt9.untdrl.util.ext.toAngle
import ktx.collections.toGdxArray

private var idCounter = 0

class Spawner(val node: GridNode, val texture: Texture) {
    val id = idCounter++
    var currentPath = DefaultGraphPath<GridNode>()
    var enemiesToSpawn = 0
    var delayBetweenEnemies = 1f
    val enemyDelayTimer = Timer(delayBetweenEnemies)

    fun spawnEnemy(wave: Int) = Enemy(
        wave,
        texture,
        node.point,
        currentPath.nodes.first().point.sub(node.point).toAngle().radDeg,
        currentPath.nodes.map(GridNode::point).toGdxArray()
    )
}
