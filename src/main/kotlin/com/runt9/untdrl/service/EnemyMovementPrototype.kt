package com.runt9.untdrl.service

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.Enemy
import com.runt9.untdrl.model.Tower

class EnemyMovementPrototype {
    private val enemies = mutableListOf<Enemy>()

    fun add(enemy: Enemy) {
        enemies += enemy
    }

    fun remove(enemy: Enemy) {
        enemies -= enemy
    }

    fun tick(delta: Float) {
        enemies.toList().forEach { enemy ->
            val steeringOutput = SteeringAcceleration(Vector2())
            enemy.behavior.calculateSteering(steeringOutput)
            if (!steeringOutput.isZero) {
                enemy.applySteering(delta, steeringOutput)
            }
        }
    }

    fun getTowerTarget(tower: Tower) =
        enemies.sortedBy { it.position.dst(7f, 4f) }
            .find { enemy ->
                tower.position.dst(enemy.position) <= 2
            }
}
