package com.runt9.untdrl.service.buildingAction

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.model.building.action.ProjectileAttackActionDefinition
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
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
    private var target: Enemy? = null
    private var attackTime = definition.attackTime
    private var damage = definition.damage
    private var range = definition.range

    private val attackTimer = Timer(attackTime)
    private val behavior = Face(building).apply {
        timeToTarget = 0.1f
        alignTolerance = 5f.degRad
        decelerationRadius = 90f.degRad
    }

    override fun act(delta: Float) {
        val steeringOutput = SteeringAcceleration(Vector2())

        attackTimer.tick(delta)

        val target = enemyService.getBuildingTarget(building.position, range) ?: return

        setTarget(target)

        behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            building.applySteering(delta, steeringOutput)
        }

        if (attackTimer.isReady && steeringOutput.isZero) {
            spawnProjectile()
            attackTimer.reset(false)
        }
    }

    // TODO: This will need to be the same in the definition for tooltips in the building menu
    override fun getStats() =
        mapOf(
            "Damage" to damage.displayInt(),
            "Attack Speed" to (1 / attackTime).displayDecimal(),
            "Range" to range.toString()
        )

    override fun levelUp(newLevel: Int) {
        // TODO: Real scaling?
        attackTime *= .9f
        attackTimer.targetTime = attackTime
        damage *= 1.1f

        if (newLevel % 5 == 0) {
            range++
        }
    }

    private fun spawnProjectile(): Projectile {
        val projectile = Projectile(building, assets[definition.projectileTexture.assetFile], damage, target!!)
        eventBus.enqueueEventSync(ProjectileSpawnedEvent(projectile))
        return projectile
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
