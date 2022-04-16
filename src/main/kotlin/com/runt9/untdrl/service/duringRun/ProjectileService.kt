package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
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
                val target = projectile.target

                if (projectile.position.dst(target.position) <= 0.1f) {
                    despawnProjectile(projectile)

                    if (!target.isAlive) return@forEach

                    logger.info { "${projectile.id}: Projectile does damage" }
                    target.takeDamage(projectile.owner, projectile.damage)
                    if (target.currentHp <= 0) {
                        logger.info { "${projectile.id}: Killed ${target.id}" }
                        target.isAlive = false
                        target.die()
                        target.affectedByBuildings.forEach { t -> t.gainXp(target.xpOnDeath) }
                        eventBus.enqueueEvent(EnemyRemovedEvent(target))
                    }
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
            logger.info { "${proj.id}: Despawning projectile" }
            proj.die()
        }
    }

    override fun stopInternal() {
        projectiles.clear()
    }
}
