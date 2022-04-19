package com.runt9.untdrl.service.buildingAction

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.building.action.ProjectileAttackActionDefinition
import com.runt9.untdrl.model.building.attackSpeed
import com.runt9.untdrl.model.building.critChance
import com.runt9.untdrl.model.building.critMulti
import com.runt9.untdrl.model.building.damage
import com.runt9.untdrl.model.building.range
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import ktx.assets.async.AssetStorage

// TODO: Once more towers start getting created, figure out how we want to abstract away common stuff
class ProjectileAttackAction(
    private val definition: ProjectileAttackActionDefinition,
    private val building: Building,
    override val eventBus: EventBus,
    private val enemyService: EnemyService,
    private val assets: AssetStorage,
    private val randomizerService: RandomizerService
) : BuildingAction {
    private val logger = unTdRlLogger()
    private var target: Enemy? = null

    private val attackTimer = Timer(building.attackSpeed)
    private val behavior = Face(building).apply {
        timeToTarget = 0.1f
        alignTolerance = 5f.degRad
        decelerationRadius = 90f.degRad
    }

    override suspend fun act(delta: Float) {
        val steeringOutput = SteeringAcceleration(Vector2())

        // Easy way to avoid another callback, just check this every tick, it's not expensive
        if (building.attackSpeed != attackTimer.targetTime) {
            attackTimer.targetTime = building.attackSpeed
        }

        attackTimer.tick(delta)

        val target = enemyService.getBuildingTarget(building.position, building.range) ?: return

        setTarget(target)

        behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            building.applySteering(delta, steeringOutput)
        }

        if (attackTimer.isReady && steeringOutput.isZero) {
            attackTimer.reset(false)
            spawnProjectile()
        }
    }

    private fun spawnProjectile(): Projectile {
        val finalDamage = rollForDamage()
        val projectile = Projectile(building, assets[definition.projectileTexture.assetFile], finalDamage, target!!)
        eventBus.enqueueEventSync(ProjectileSpawnedEvent(projectile))
        return projectile
    }

    private fun rollForDamage(): Float {
        val roll = randomizerService.rng.nextInt(0, 100)
        val isCrit = roll / 100f <= building.critChance
        var damageMulti = randomizerService.rng.nextInt(90, 111).toFloat() / 100f
        if (isCrit) damageMulti *= building.critMulti
        return building.damage * damageMulti
    }

    private fun setTarget(target: Enemy) {
        this.target = target
        behavior.target = target
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveEnd() {
        attackTimer.reset(false)
    }
}
