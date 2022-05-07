package com.runt9.untdrl.service.towerAction.subAction

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.util.ext.angleToWithin
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.dynamicInject
import com.runt9.untdrl.util.ext.dynamicInjectCheckAssignableFrom

class FaceTargetSubAction(private val tower: Tower, private val enemyService: EnemyService) : TowerSubAction {
    private val angleToTargetLimit = 3f
    var target: Enemy? = null
    private val behavior = Face(tower).apply {
        timeToTarget = 0.01f
        alignTolerance = 1f.degRad
        decelerationRadius = 45f.degRad
    }

    override suspend fun act(delta: Float) {
        // TODO: Target switching every frame seems really awful
        val target = enemyService.getTowerTarget(tower.position, tower.range, tower.targetingMode) ?: return

        this.target = target
        behavior.target = target

        val steeringOutput = SteeringAcceleration(Vector2())
        behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            tower.applySteering(delta, steeringOutput)
        }
    }

    fun canAttackTarget(): Boolean {
        return target?.let { t -> tower.angleToWithin(t, angleToTargetLimit) && tower.inRangeOf(t.position) && t.isAlive } ?: false
    }
}

fun faceTarget(tower: Tower) = dynamicInject(FaceTargetSubAction::class, dynamicInjectCheckAssignableFrom(Tower::class.java) to tower)
