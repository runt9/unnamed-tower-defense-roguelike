package com.runt9.untdrl.service

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.util.ext.unTdRlLogger

// TODO: Thoughts:
//   - The first node after the spawn _must_ branch
//   - Branching chance goes down the more branches that have happened
//   - Paths should favor going forward over curving
//   - Paths should not be able to get "stuck" and loop back into themselves
//   - Paths should generally seek the edges
//   - The first two nodes after a spawner cannot be an edge node
class ChunkGeneratorPrototype {
    private val logger = unTdRlLogger()

    fun generateGrid(): Array<IntArray> {
        val grid = Array(8) { IntArray(8) }
        val visited = mutableListOf<Vector2>()

        val randomRow = (1..6).random()
        val randomCol = (1..6).random()

        grid[randomRow][randomCol] = 2
        visited += Vector2(randomCol.toFloat(), randomRow.toFloat())

        var previousNode = visited[0].cpy()
        logger.info { "Starting at $previousNode" }

        while (true) {
            if (previousNode.x == 0f || previousNode.x == 7f || previousNode.y == 0f || previousNode.y == 7f) {
                break
            }

            val adjacentNodes = listOf(
                Vector2(previousNode.x - 1, previousNode.y),
                Vector2(previousNode.x + 1, previousNode.y),
                Vector2(previousNode.x, previousNode.y - 1),
                Vector2(previousNode.x, previousNode.y + 1)
            ).subtract(visited.toSet())

            if (adjacentNodes.isEmpty()) {
                break
            }

            // TODO: Attempt to weight forward direction more than turning
            val nextNode = adjacentNodes.random()
            logger.info { "Next node: $nextNode" }
            grid[nextNode.y.toInt()][nextNode.x.toInt()] = 1
            previousNode = nextNode.cpy()
            visited += previousNode.cpy()
        }

        logger.info { "Final Path: $visited" }

        return grid
    }
}
