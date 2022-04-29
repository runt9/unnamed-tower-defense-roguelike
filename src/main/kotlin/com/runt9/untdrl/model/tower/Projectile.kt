package com.runt9.untdrl.model.tower

import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.behaviors.Pursue
import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.ext.BaseSteerable
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.positionToLocation
import com.runt9.untdrl.util.ext.toVector

private var idCounter = 0

class Projectile(
    val owner: Tower,
    val texture: UnitTexture,
    val target: Enemy,
    pierce: Int = 0,
    var homing: Boolean = true,
    val degreesFromCenter: Float = 0f,
    speed: Float = 10f
) : BaseSteerable(owner.position, owner.rotation) {
    val id = idCounter++
    override val linearSpeedLimit = speed
    override val linearAccelerationLimit = maxLinearSpeed * 100f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 10f
    override val boundingBoxRadius = 0.125f

    private lateinit var onDieCb: suspend Projectile.() -> Unit

    var remainingPierces = pierce
    val maxTravelDistance = owner.range
    var travelDistance = 0f
    val collidedWith = mutableListOf<Enemy>()
    
    fun onDie(onDieCb: suspend Projectile.() -> Unit) {
        this.onDieCb = onDieCb
    }

    var behavior = calculateBehavior()

    fun calculateBehavior(): BlendedSteering<Vector2> {
        val steering = BlendedSteering(this)
        val look = LookWhereYouAreGoing(this).apply {
            timeToTarget = 0.01f
            alignTolerance = 0f.degRad
            decelerationRadius = 45f.degRad
        }
        steering.add(BlendedSteering.BehaviorAndWeight(look, 1f))

        if (homing) {
            val pursue = Pursue(this, target)
            steering.add(BlendedSteering.BehaviorAndWeight(pursue, 1f))
        } else {
            val goToPosition = position.cpy().add((rotation + degreesFromCenter).degRad.toVector(Vector2(0f, 0f)).scl(owner.range))
            val seek = Seek(this, positionToLocation(goToPosition))

            steering.add(BlendedSteering.BehaviorAndWeight(seek, 1f))
        }

        return steering
    }

    suspend fun die() {
        onDieCb()
    }
}
