package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileService(
    private val eventBus: EventBus,
    registry: RunServiceRegistry,
    private val buildingService: BuildingService,
    private val enemyService: EnemyService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val projectiles = mutableListOf<Projectile>()

    fun add(projectile: Projectile) = runOnServiceThread {
        projectiles += projectile
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() = runOnServiceThread {
        projectiles.forEach { it.die() }
        projectiles.clear()
    }

    override fun tick(delta: Float) {
        runOnServiceThread {
            projectiles.toList().forEach { projectile ->
                // Projectiles move towards their target, but if they collide with another enemy first, they'll damage that enemy instead
                val collidedEnemy = enemyService.collidesWithEnemy(projectile.position, 0.1f)

                if (collidedEnemy != null) {
                    // TODO: Handle piercing projectiles
                    despawnProjectile(projectile)

                    if (!collidedEnemy.isAlive) return@forEach

                    collidedEnemy.takeDamage(projectile.owner, projectile.damage)
                    if (collidedEnemy.currentHp <= 0) {
                        collidedEnemy.isAlive = false
                        collidedEnemy.affectedByBuildings.forEach { t -> buildingService.gainXp(t, collidedEnemy.xpOnDeath) }
                        eventBus.enqueueEvent(EnemyRemovedEvent(collidedEnemy))
                    }
                    return@forEach
                }

                // Reached the target without colliding with anything, so fizzle out
                if (projectile.position.dst(projectile.target.position) <= 0.1f) {
                    despawnProjectile(projectile)
                    return@forEach
                }

                val steeringOutput = SteeringAcceleration(Vector2())
                projectile.behavior.calculateSteering(steeringOutput)
                if (!steeringOutput.isZero) {
                    projectile.applySteering(delta, steeringOutput)
                }
            }
        }
    }

    private fun despawnProjectile(proj: Projectile) {
        if (projectiles.remove(proj)) {
            proj.die()
        }
    }

    override fun stopInternal() {
        projectiles.clear()
    }
}
