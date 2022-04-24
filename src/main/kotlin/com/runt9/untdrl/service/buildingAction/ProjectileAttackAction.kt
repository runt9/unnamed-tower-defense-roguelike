package com.runt9.untdrl.service.buildingAction

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.building.action.ProjectileAttackActionDefinition
import com.runt9.untdrl.model.building.attackTime
import com.runt9.untdrl.model.building.intercept.InterceptorHook
import com.runt9.untdrl.model.building.intercept.OnAttack
import com.runt9.untdrl.model.building.range
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.util.ext.LocationAdapter
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
    private val assets: AssetStorage
) : BuildingAction {
    private val logger = unTdRlLogger()
    private var target: Enemy? = null

    private val attackTimer = Timer(building.attackTime)
    private val behavior = Face(building).apply {
        timeToTarget = 0.01f
        alignTolerance = 1f.degRad
        decelerationRadius = 45f.degRad
    }
    var pierce = definition.pierce

    override suspend fun act(delta: Float) {
        val steeringOutput = SteeringAcceleration(Vector2())

        // Easy way to avoid another callback, just check this every tick, it's not expensive
        if (building.attackTime != attackTimer.targetTime) {
            attackTimer.targetTime = building.attackTime
        }

        attackTimer.tick(delta)

        val target = enemyService.getBuildingTarget(building.position, building.range, building.targetingMode) ?: return

        setTarget(target)

        behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            building.applySteering(delta, steeringOutput)
        }

        if (attackTimer.isReady && steeringOutput.isZero) {
            attackTimer.reset(false)
            spawnProjectile()
            building.intercept(InterceptorHook.ON_ATTACK, OnAttack(building))
        }
    }

    private fun spawnProjectile(): Projectile {
        val projectile = Projectile(building, assets[definition.projectileTexture.assetFile], pierce)
        eventBus.enqueueEventSync(ProjectileSpawnedEvent(projectile))
        return projectile
    }

    private fun setTarget(target: Enemy) {
        this.target = target
        // TODO: Replace this with better prediction based off of the PathFollow
        val predictedTarget = target.position.cpy().mulAdd(target.linearVelocity.cpy(), 0.35f)
        behavior.target = object : LocationAdapter() {
            override fun getPosition() = predictedTarget
        }
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveEnd() {
        attackTimer.reset(false)
    }
}
