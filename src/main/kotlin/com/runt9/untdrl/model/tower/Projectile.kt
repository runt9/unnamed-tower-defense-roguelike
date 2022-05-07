package com.runt9.untdrl.model.tower

import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.behaviors.Pursue
import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.ext.BaseSteerable
import com.runt9.untdrl.util.ext.Size
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.positionToLocation
import com.runt9.untdrl.util.ext.toVector

private var idCounter = 0

private fun calculateStartingPosition(towerPos: Vector2, towerRot: Float, size: Size): Vector2 {
    val rotationVector = towerRot.degRad.toVector(Vector2.Zero.cpy())
    val pointedVector = towerPos.cpy().add(rotationVector).nor()
    val scaledVector = pointedVector.scl(size.width, size.height)
    return towerPos.cpy().add(scaledVector.scl(0.2f))
}

class Projectile(
    val owner: Tower,
    val texture: TextureDefinition,
    val target: Enemy,
    pierce: Int = 0,
    var homing: Boolean = true,
    degreesFromCenter: Float = 0f,
    speed: Float = 10f,
    var delayedHoming: Float = 0f,
    var size: Size = Size(0.25f, 0.25f),
    private val boundingPolygon: Polygon = generateDefaultPolygon()
) : BaseSteerable(calculateStartingPosition(owner.position, owner.rotation, size), owner.rotation + degreesFromCenter) {
    val id = idCounter++
    override val linearSpeedLimit = speed
    override val linearAccelerationLimit = maxLinearSpeed * 100f
    override val angularSpeedLimit = 5f
    override val angularAccelerationLimit = angularSpeedLimit * 10f
    override val boundingBoxRadius = 0.125f

    private lateinit var onDieCb: suspend Projectile.() -> Unit

    var remainingPierces = pierce
    val maxTravelDistance = owner.range
    var travelDistance = 0f
    val collidedWith = mutableListOf<Enemy>()
    val bounds: Polygon get() {
        boundingPolygon.setPosition(position.x, position.y)
        boundingPolygon.rotation = rotation
        return boundingPolygon
    }

    fun onDie(onDieCb: suspend Projectile.() -> Unit) {
        this.onDieCb = onDieCb
    }

    var behavior = calculateBehavior()


    private fun calculateBehavior(): BlendedSteering<Vector2> {
        val steering = BlendedSteering(this)
        val look = LookWhereYouAreGoing(this).apply {
            timeToTarget = 0.01f
            alignTolerance = 0f.degRad
            decelerationRadius = 45f.degRad
        }
        steering.add(BlendedSteering.BehaviorAndWeight(look, 2f))

        if (homing) {
            val pursue = Pursue(this, target, 0.25f)
            steering.add(BlendedSteering.BehaviorAndWeight(pursue, 1f))
        } else {
            val goToPosition = position.cpy().add((rotation).degRad.toVector(Vector2(0f, 0f)).scl(owner.range))
            val seek = Seek(this, positionToLocation(goToPosition))

            steering.add(BlendedSteering.BehaviorAndWeight(seek, 1f))
        }

        return steering
    }

    fun recalculateBehavior() {
        behavior = calculateBehavior()
    }

    suspend fun die() {
        onDieCb()
    }
}

fun generateDefaultPolygon(): Polygon {
    val top = Vector2(0f, 0.125f)
    val bottomRight = Vector2(0.125f, -0.125f)
    val bottomCenter = Vector2(0f, -0.0625f)
    val bottomLeft = Vector2(-0.125f, -0.125f)

    return Polygon(FloatArray(8).apply {
        this[0] = top.x
        this[1] = top.y
        this[2] = bottomRight.x
        this[3] = bottomRight.y
        this[4] = bottomCenter.x
        this[5] = bottomCenter.y
        this[6] = bottomLeft.x
        this[7] = bottomLeft.y
    })
}
