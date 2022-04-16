package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.event.EnemyHpChanged
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.ProjectileReadyEvent
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val projectiles = mutableListOf<Projectile>()

    @HandlesEvent
    fun add(event: ProjectileReadyEvent) = runOnServiceThread {
        logger.info { "Adding projectile ${event.projectile.id}" }
        projectiles += event.projectile
    }

    @HandlesEvent
    fun enemyRemovedEvent(event: EnemyRemovedEvent) = runOnServiceThread {
        projectiles.filter { it.target == event.enemy }.forEach {
            despawnProjectile(it)
        }
    }

    override fun tick(delta: Float) {
        runOnServiceThread {
            projectiles.toList().forEach { projectile ->
                val target = projectile.target

                if (!target.isAlive) {
                    despawnProjectile(projectile)
                    return@forEach
                }

                if (projectile.position.dst(target.position) <= 0.1f) {
                    logger.info { "${projectile.id}: Projectile does damage" }
                    target.takeDamage(projectile.owner, projectile.damage)
                    despawnProjectile(projectile)
                    eventBus.enqueueEventSync(EnemyHpChanged(target))
                    if (target.currentHp <= 0) {
                        logger.info { "${projectile.id}: Killed ${target.id}" }
                        target.isAlive = false
                        target.die()
                        target.affectedByBuildings.forEach { t -> t.gainXp(target.xpOnDeath) }
                        eventBus.enqueueEventSync(EnemyRemovedEvent(target))

                        projectiles.filter { it.target == target }.forEach {
                            despawnProjectile(it)
                        }
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

    private suspend fun despawnProjectile(proj: Projectile) {
        if (projectiles.remove(proj)) {
            logger.info { "${proj.id}: Despawning projectile" }
            proj.die()
        }
    }

    override fun stopInternal() {
        projectiles.clear()
    }
}
