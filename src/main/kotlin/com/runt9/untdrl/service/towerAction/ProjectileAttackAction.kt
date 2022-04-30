package com.runt9.untdrl.service.towerAction

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.ai.utils.ArithmeticUtils
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Projectile
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.action.ProjectileAttackActionDefinition
import com.runt9.untdrl.model.tower.attackTime
import com.runt9.untdrl.model.tower.intercept.InterceptorHook
import com.runt9.untdrl.model.tower.intercept.OnAttack
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.toVector
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.abs

// TODO: Once more towers start getting created, figure out how we want to abstract away common stuff
class ProjectileAttackAction(
    private val definition: ProjectileAttackActionDefinition,
    private val tower: Tower,
    override val eventBus: EventBus,
    private val enemyService: EnemyService
) : TowerAction {
    private val logger = unTdRlLogger()
    private var target: Enemy? = null

    private val attackTimer = Timer(tower.attackTime)
    private val behavior = Face(tower).apply {
        timeToTarget = 0.01f
        alignTolerance = 1f.degRad
        decelerationRadius = 45f.degRad
    }
    var pierce = definition.pierce
    var homing = definition.homing
    var speed = definition.speed
    var delayedHoming = definition.delayedHoming
    var anglePerProjectile = definition.anglePerProjectile

    override suspend fun act(delta: Float) {
        val steeringOutput = SteeringAcceleration(Vector2())

        // Easy way to avoid another callback, just check this every tick, it's not expensive
        if (tower.attackTime != attackTimer.targetTime) {
            attackTimer.targetTime = tower.attackTime
        }

        attackTimer.tick(delta)

        val target = enemyService.getTowerTarget(tower.position, tower.range, tower.targetingMode) ?: return

        setTarget(target)

        behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            tower.applySteering(delta, steeringOutput)
        }

        val rotationVector = ArithmeticUtils.wrapAngleAroundZero(tower.rotation.degRad).toVector(Vector2.Zero).angleDeg()
        val positionVector = behavior.target.position.cpy().sub(tower.position.cpy()).nor().angleDeg()

        if (attackTimer.isReady && abs(rotationVector - positionVector) <= 3f) {
            attackTimer.reset(false)
            spawnProjectiles()
            tower.intercept(InterceptorHook.ON_ATTACK, OnAttack(tower))
        }
    }

    private fun spawnProjectiles() {
        val projCount = tower.attrs[AttributeType.PROJECTILE_COUNT]?.invoke() ?: 1
        // TODO: This calculation is wrong, a projectile should shoot from center _only_ if there is an odd number of total projectiles
        repeat(projCount.toInt()) { i ->
            var degreesFromCenter = ((i + 1) / 2) * anglePerProjectile
            if (i % 2 == 0) degreesFromCenter *= -1
            val projectile = Projectile(tower, definition.projectileTexture, target!!, pierce, homing, degreesFromCenter, speed, delayedHoming)
            eventBus.enqueueEventSync(ProjectileSpawnedEvent(projectile))
        }
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
