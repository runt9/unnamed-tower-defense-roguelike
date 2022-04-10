package com.runt9.untdrl.service

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.Projectile
import com.runt9.untdrl.model.Tower
import com.runt9.untdrl.util.ext.unTdRlLogger

class TowerAttackPrototype(private val enemyPrototype: EnemyMovementPrototype) {
    private val logger = unTdRlLogger()
    private val towers = mutableListOf<Tower>()
    private val projectiles = mutableListOf<Projectile>()

    fun add(tower: Tower) {
        towers += tower
    }

    fun remove(tower: Tower) {
        towers -= tower
    }

    fun tick(delta: Float) {
        towers.forEach { tower ->
            val steeringOutput = SteeringAcceleration(Vector2())

            tower.attackTimer.tick(delta)

            val target = enemyPrototype.getTowerTarget(tower) ?: return@forEach

            tower.setTarget(target)

            tower.behavior.calculateSteering(steeringOutput)
            if (!steeringOutput.isZero) {
                tower.applySteering(delta, steeringOutput)
            }

            if (tower.attackTimer.isReady && steeringOutput.isZero) {
                logger.info { "Tower attacks!" }
                projectiles += tower.spawnProjectile()
                tower.attackTimer.reset(false)
            }
        }

        val projectilesToRemove = mutableSetOf<Projectile>()
        projectiles.forEach {
            if (!tickProjectile(it, delta)) {
                projectilesToRemove += it
            }
        }

        projectiles -= projectilesToRemove
    }

    private fun tickProjectile(projectile: Projectile, delta: Float): Boolean {
        if (projectile.position.dst(projectile.target.position) <= 0.1f) {
            logger.info { "Removing projectile" }
            projectile.die()
            return false
        }

        val steeringOutput = SteeringAcceleration(Vector2())
        projectile.behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            projectile.applySteering(delta, steeringOutput)
        }

        return true
    }

    fun isNoTowerPositionOverlap(tower: Tower) = towers.none { it.position == tower.position }
}
