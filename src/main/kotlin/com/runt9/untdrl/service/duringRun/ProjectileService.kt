package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.building.intercept.DamageRequest
import com.runt9.untdrl.model.building.intercept.DamageResult
import com.runt9.untdrl.model.building.intercept.InterceptorHook
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileService(
    private val eventBus: EventBus,
    registry: RunServiceRegistry,
    private val buildingService: BuildingService,
    private val enemyService: EnemyService,
    private val randomizer: RandomizerService
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
                // Projectiles move towards their target, but if they collide with another enemy first, they'll damage that enemy instead
                val collidedEnemy = enemyService.collidesWithEnemy(projectile.position, 0.1f)

                // TODO: Handle AoE
                if (collidedEnemy != null) {
                    // TODO: Handle piercing projectiles
                    despawnProjectile(projectile)

                    if (!collidedEnemy.isAlive) return@forEach

                    calculateDamage(projectile.owner, collidedEnemy)
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

    // TODO: Move these somewhere else, they're not projectile-specific
    private fun calculateDamage(building: Building, enemy: Enemy) {
        val damageRequest = DamageRequest(building)
        building.intercept(InterceptorHook.BEFORE_DAMAGE, damageRequest)
        logger.info { "Final Damage Request: $damageRequest" }
        val damageResult = rollForDamage(damageRequest)
        building.intercept(InterceptorHook.AFTER_DAMAGE, damageResult)
        logger.info { "Final Damage Result: $damageResult" }
        val totalDamage = building.damageTypes.map { dt -> (damageResult.totalDamage * dt.pctOfBase) / (enemy.getResistance(dt.type, dt.penetration)) }.sum()
        enemy.takeDamage(building, totalDamage)
    }

    private fun rollForDamage(request: DamageRequest): DamageResult {
        val isCrit = randomizer.percentChance(request.totalCritChance)
        var damageMulti = randomizer.rng.nextInt(90, 111).toFloat() / 100f
        if (isCrit) damageMulti *= request.totalCritMulti
        damageMulti *= request.totalDamageMulti
        return DamageResult(request.totalBaseDamage, damageMulti)
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
