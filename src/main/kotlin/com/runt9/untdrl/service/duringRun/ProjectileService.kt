package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.building.intercept.DamageRequest
import com.runt9.untdrl.model.building.intercept.DamageResult
import com.runt9.untdrl.model.building.intercept.InterceptorHook
import com.runt9.untdrl.model.building.intercept.ResistanceRequest
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
                val collidedEnemy = enemyService.collidesWithEnemy(projectile.position, 0.25f)

                // TODO: Handle AoE
                if (collidedEnemy != null && !projectile.collidedWith.contains(collidedEnemy)) {
                    projectile.collidedWith += collidedEnemy
                    if (collidedEnemy.isAlive) {
                        calculateDamage(projectile.owner, collidedEnemy)
                        if (collidedEnemy.currentHp <= 0) {
                            collidedEnemy.isAlive = false
                            collidedEnemy.affectedByBuildings.forEach { t -> buildingService.gainXp(t, collidedEnemy.xpOnDeath) }
                            eventBus.enqueueEvent(EnemyRemovedEvent(collidedEnemy))
                        }
                    }

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

    // TODO: Move these somewhere else, they're not projectile-specific
    private fun calculateDamage(building: Building, enemy: Enemy) {
        val damageRequest = DamageRequest(building)
        building.intercept(InterceptorHook.BEFORE_DAMAGE_CALC, damageRequest)
        logger.info { "Final Damage Request: $damageRequest" }
        val damageResult = rollForDamage(damageRequest)
        building.intercept(InterceptorHook.AFTER_DAMAGE_CALC, damageResult)
        logger.info { "Final Damage Result: $damageResult" }
        val resistanceRequest = ResistanceRequest(building.damageTypes.toList(), enemy.resistances.toMap(), damageResult)
        building.intercept(InterceptorHook.BEFORE_RESISTS, resistanceRequest)
        enemy.takeDamage(building, resistanceRequest.finalDamage)

        building.procs.filter { randomizer.percentChance(it.chance) }.forEach { proc -> proc.applyToEnemy(enemy) }
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
