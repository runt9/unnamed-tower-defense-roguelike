package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Projectile
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileService(
    eventBus: EventBus,
    registry: RunServiceRegistry,
    private val enemyService: EnemyService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val projectiles = mutableListOf<Projectile>()

    fun add(projectile: Projectile) = launchOnServiceThread {
        projectiles += projectile
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() = launchOnServiceThread {
        projectiles.forEach { it.die() }
        projectiles.clear()
    }

    override fun tick(delta: Float) {
        launchOnServiceThread {
            projectiles.toList().forEach { projectile ->
                val collidedEnemy = enemyService.collidesWithEnemy(projectile.bounds)

                if (collidedEnemy != null && !projectile.collidedWith.contains(collidedEnemy)) {
                    projectile.collidedWith += collidedEnemy
                    enemyService.attackEnemy(DamageSource.PROJECTILE, projectile.owner, collidedEnemy, projectile.position)

                    if (projectile.remainingPierces-- <= 0) {
                        despawnProjectile(projectile)
                        return@forEach
                    }
                }

                // TODO: This probably needs to get broken into "ProjectileConfiguration" that can be composed by tower definitions to control
                //  how that tower's projectiles work and do the typical interceptor + hook type setup
                // If the projectile is homing and the target is gone, we need to disable homing and just finish moving towards the edge of the range circle
                // We also need to disable homing if we collided with an enemy and didn't despawn since this means we pierced and now just need to keep moving
                if (projectile.homing && (!projectile.target.isAlive || collidedEnemy != null)) {
                    projectile.homing = false
                    projectile.recalculateBehavior()
                } else if (!projectile.homing && projectile.delayedHoming > 0 && projectile.travelDistance >= projectile.delayedHoming) {
                    projectile.homing = true
                    projectile.recalculateBehavior()
                    projectile.delayedHoming = 0f
                }

                val steeringOutput = SteeringAcceleration(Vector2())
                projectile.behavior.calculateSteering(steeringOutput)
                if (!steeringOutput.isZero) {
                    val beforePosition = projectile.position.cpy()
                    projectile.applySteering(delta, steeringOutput)
                    val distanceTravelled = projectile.position.dst(beforePosition)
                    projectile.travelDistance += distanceTravelled
                }

                if (projectile.travelDistance >= projectile.maxTravelDistance) {
                    despawnProjectile(projectile)
                    return@forEach
                }
            }
        }
    }

    private suspend fun despawnProjectile(proj: Projectile) {
        if (projectiles.remove(proj)) {
            proj.die()
        }
    }

    override fun stopInternal() {
        projectiles.clear()
    }
}
