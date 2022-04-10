package com.runt9.untdrl.model.path

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.model.Spawner
import com.runt9.untdrl.service.duringRun.RunService
import com.runt9.untdrl.service.duringRun.RunServiceRegistry
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import ktx.collections.GdxArray
import ktx.collections.isNotEmpty
import ktx.collections.toGdxArray
import kotlin.math.abs

class IndexedGridGraph(eventBus: EventBus, registry: RunServiceRegistry) : IndexedGraph<GridNode>, RunService(eventBus, registry) {
    private val logger = unTdRlLogger()

    lateinit var home: GridNode
    private val nodes: MutableMap<Vector2, GridNode> = mutableMapOf()
    val spawners = mutableListOf<Spawner>()

    override fun getNodeCount() = nodes.size
    override fun getIndex(node: GridNode) = node.index

    // TODO: Just make sure this isn't slow, probably should cache and recalculate each wave
    override fun getConnections(fromNode: GridNode): GdxArray<Connection<GridNode>> =
        fromNode.adjacentNodes().filter { it.type != GridNodeType.EMPTY }.map { DefaultConnection(fromNode, it) }.toGdxArray()

    operator fun MutableMap<Vector2, GridNode>.get(x: Number, y: Number): GridNode? = get(Vector2(x.toFloat(), y.toFloat()))

    fun addChunk(chunk: Chunk) {
        val xOffset = chunk.position.x - 4
        val yOffset = chunk.position.y - 4

        chunk.grid.forEachIndexed { ri, row ->
            row.forEachIndexed { ci, col ->
                val node = GridNode(ci + xOffset, ri + yOffset, nodes.size, GridNodeType.values()[col])
                nodes[node.point.cpy()] = node

                if (node.type == GridNodeType.HOME) {
                    home = node
                } else if (node.type == GridNodeType.SPAWNER) {
                    spawners += Spawner(node)
                }
            }
        }
    }

    private fun GridNode.adjacentNodes(): List<GridNode> {
        return listOfNotNull(
            nodes[x - 1, y],
            nodes[x + 1, y],
            nodes[x, y - 1],
            nodes[x, y + 1]
        )
    }

    fun recalculateSpawnerPaths() {
        spawners.forEach { spawner ->
            val newPath = DefaultGraphPath<GridNode>()
            IndexedAStarPathFinder(this).searchNodePath(spawner.node, home, { sn, en -> abs(en.x - sn.x) + abs(en.y - sn.y) }, newPath)
            newPath.nodes.removeIndex(0)
            spawner.currentPath = newPath
            logger.info { "Found spawner path ${spawner.currentPath.nodes.map { it.point }.toList()}" }
        }
    }

    fun isValidChunkPlacement(chunk: Chunk): Boolean {
        val xOffset = chunk.position.x - 4
        val yOffset = chunk.position.y - 4

        var foundValidPath = false

        chunk.grid.forEachIndexed { ri, row ->
            row.forEachIndexed { ci, col ->
                val node = GridNode(ci + xOffset, ri + yOffset, nodes.size, GridNodeType.values()[col])

                if (nodes.containsKey(node.point)) {
                    return false
                }

                if (!foundValidPath && node.type == GridNodeType.PATH &&  getConnections(node).isNotEmpty()) {
                    foundValidPath = true
                }
            }
        }

        return foundValidPath
    }

    fun isEmptyTile(position: Vector2) = nodes[position]?.type == GridNodeType.EMPTY

    override fun stopInternal() {
        nodes.clear()
        spawners.clear()
    }
}
