package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Projectile
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.DamageRequest
import com.runt9.untdrl.model.tower.intercept.DamageResult
import com.runt9.untdrl.model.tower.intercept.InterceptorHook
import com.runt9.untdrl.model.tower.intercept.ResistanceRequest
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileService(
    private val eventBus: EventBus,
    registry: RunServiceRegistry,
    private val towerService: TowerService,
    private val enemyService: EnemyService,
    private val attackService: AttackService
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
                val collidedEnemy = enemyService.collidesWithEnemy(projectile.position, 0.25f)

                // TODO: Handle AoE
                if (collidedEnemy != null && !projectile.collidedWith.contains(collidedEnemy)) {
                    projectile.collidedWith += collidedEnemy
                    attackService.attackEnemy(projectile.owner, collidedEnemy)

                    if (projectile.remainingPierces-- == 0) {
                        despawnProjectile(projectile)
                        return@forEach
                    }
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
