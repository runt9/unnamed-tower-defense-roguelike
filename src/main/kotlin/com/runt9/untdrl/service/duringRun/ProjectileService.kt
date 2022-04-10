package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.Projectile
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus

class ProjectileService(eventBus: EventBus, registry: RunServiceRegistry) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val projectiles = mutableListOf<Projectile>()

    fun add(projectile: Projectile) {
        projectiles += projectile
    }

    fun remove(projectile: Projectile) {
        projectiles -= projectile
    }

    override fun tick(delta: Float) {
        projectiles.toList().forEach { projectile ->
            if (projectile.position.dst(projectile.target.position) <= 0.1f) {
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

    override fun stopInternal() {
        projectiles.clear()
    }
}
