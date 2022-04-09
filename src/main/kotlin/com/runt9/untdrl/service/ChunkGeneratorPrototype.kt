package com.runt9.untdrl.service

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.view.duringRun.CHUNK_SIZE

class ChunkGeneratorPrototype {
    private val logger = unTdRlLogger()

    fun buildHomeChunk(): Array<IntArray> {
        val grid = Array(CHUNK_SIZE) { IntArray(CHUNK_SIZE) }

        val midPoint = ((CHUNK_SIZE - 1) / 2)
        grid[midPoint][midPoint] = 2

        (0 until CHUNK_SIZE).filter { it != midPoint }.forEach {
            grid[midPoint][it] = 1
            grid[it][midPoint] = 1
        }

        return grid
    }

    fun generateGrid(): Array<IntArray> {
        val grid = Array(CHUNK_SIZE) { IntArray(CHUNK_SIZE) }
        val visited = mutableListOf<Vector2>()

        val randomRow = (2..CHUNK_SIZE - 3).random()
        val randomCol = (2..CHUNK_SIZE - 3).random()

        grid[randomRow][randomCol] = 3
        visited += Vector2(randomCol.toFloat(), randomRow.toFloat())

        logger.info { "Spawner at ${visited[0]}" }

        val firstStep = getFirstStep(visited[0])
        grid[firstStep.y.toInt()][firstStep.x.toInt()] = 1
        visited.add(firstStep)

        logger.info { "First step $firstStep" }

        buildBranch(firstStep, visited, grid)
        buildBranch(firstStep, visited, grid)
        buildBranch(firstStep, visited, grid)

        logger.info { "Final Path: $visited" }

        return grid
    }

    private fun getFirstStep(node: Vector2): Vector2 {
        return node.adjacentNodes.filter { adjNode -> !adjNode.isEdgeNode && adjNode.adjacentNodes.none { it.isEdgeNode } }.random()
    }

    fun buildBranch(startingNode: Vector2, visited: MutableList<Vector2>, grid: Array<IntArray>) {
        var previousNode = startingNode.cpy()
        logger.info { "Branching from $previousNode" }

        var firstNode = true
        val validBranch: Boolean
        val branch = mutableListOf<Vector2>()

        while (true) {
            if (previousNode.isEdgeNode) {
                logger.info { "$previousNode is edge node, branch complete" }
                validBranch = true
                break
            }

            val adjacentNodes = previousNode.getNextValidAdjacents(visited + branch, firstNode)

            if (adjacentNodes.isEmpty()) {
                logger.info { "$previousNode has no valid adjacent node, branch complete" }
                validBranch = false
                break
            }

            val nextNode = adjacentNodes.random()
            logger.info { "Next node: $nextNode" }
            previousNode = nextNode.cpy()
            branch += previousNode
            firstNode = false
        }

        if (validBranch) {
            logger.info { "Valid branch created: $branch" }
            visited += branch
            branch.forEach { node ->
                grid[node.y.toInt()][node.x.toInt()] = 1

                while ((1..10).random() >= 5 && !node.isEdgeNode && node.adjacentNodes.none { it.isEdgeNode } && node.getNextValidAdjacents(visited, true).isNotEmpty()) {
                    buildBranch(node, visited, grid)
                }
            }
        }
    }

    val Vector2.adjacentNodes
        get() = listOf(
            Vector2(x - 1, y),
            Vector2(x + 1, y),
            Vector2(x, y - 1),
            Vector2(x, y + 1)
        )

    val Vector2.isEdgeNode get() = x == 0f || x == CHUNK_SIZE - 1f || y == 0f || y == CHUNK_SIZE - 1f

    fun Vector2.getNextValidAdjacents(visited: List<Vector2>, firstNode: Boolean): List<Vector2> {
        val self = this

        return adjacentNodes.filter { node2 ->
            // Next node cannot be previously visited
            if (visited.contains(node2)) {
                return@filter false
            }

            // Get intersection between the next node's adjacent nodes and where we've already been, excluding
            // the node we're pathing from. We cannot go to a node that is adjacent to a previously visited node
            val intersect = node2.adjacentNodes.intersect(visited.toSet()).filter { it != self }

            return@filter intersect.isEmpty() && (!firstNode || !node2.isEdgeNode)
        }
    }
}
