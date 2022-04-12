package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.event.EnemyHpChanged
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.tower.Projectile
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileService(private val eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val projectiles = mutableListOf<Projectile>()

    fun add(projectile: Projectile) {
        projectiles += projectile
    }

    fun remove(projectile: Projectile) {
        projectiles -= projectile
    }

    @HandlesEvent
    fun enemyRemovedEvent(event: EnemyRemovedEvent) = runOnServiceThread {
        projectiles.filter { it.target == event.enemy }.forEach {
            it.die()
            projectiles.remove(it)
        }
    }

    override fun tick(delta: Float) {
        runOnServiceThread {
            projectiles.toList().forEach { projectile ->
                if (projectile.position.dst(projectile.target.position) <= 0.1f) {
                    projectile.target.takeDamage(projectile.damage)
                    eventBus.enqueueEvent(EnemyHpChanged(projectile.target))
                    if (projectile.target.currentHp <= 0) {
                        eventBus.enqueueEvent(EnemyRemovedEvent(projectile.target))
                    }
                    projectile.die()
                    projectiles.remove(projectile)
                }

                val steeringOutput = SteeringAcceleration(Vector2())
                projectile.behavior.calculateSteering(steeringOutput)
                if (!steeringOutput.isZero) {
                    projectile.applySteering(delta, steeringOutput)
                }
            }
        }
    }

    override fun stopInternal() {
        projectiles.clear()
    }
}
