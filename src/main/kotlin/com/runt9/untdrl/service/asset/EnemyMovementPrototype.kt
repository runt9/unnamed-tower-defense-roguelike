package com.runt9.untdrl.service.asset

import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.FollowPath
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.utils.paths.LinePath
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.radDeg
import com.runt9.untdrl.util.ext.toAngle
import com.runt9.untdrl.util.ext.toVector
import ktx.collections.gdxArrayOf

class Enemy(val initialPosition: Vector2, val initialRotation: Float) : SteerableAdapter<Vector2>() {
    private val position = initialPosition.cpy()!!
    var rotation: Float = initialRotation
    private val linearVelocity = Vector2()
    private val maxLinearSpeed = 1f
    private val maxLinearAcceleration = maxLinearSpeed * 100f
    private val maxAngularSpeed = 8f
    private val maxAngularAcceleration = maxAngularSpeed * 100f
    private var angularVelocity = 0f
    private val boundingRadius = 0.375f
    private var tagged = true

    val path = gdxArrayOf(
        Vector2(0f, 1f),
        Vector2(1f, 1f),
        Vector2(2f, 1f),
        Vector2(3f, 1f),
        Vector2(4f, 1f),
        Vector2(4f, 2f),
        Vector2(4f, 3f),
        Vector2(5f, 3f),
        Vector2(6f, 3f),
        Vector2(7f, 3f),
        Vector2(8f, 3f),
        Vector2(8f, 4f),
        Vector2(9f, 4f),
        Vector2(10f, 4f),
        Vector2(11f, 4f),
        Vector2(11f, 5f),
        Vector2(11f, 6f),
        Vector2(11f, 7f),
        Vector2(12f, 7f),
        Vector2(13f, 7f),
        Vector2(14f, 7f),
        Vector2(15f, 7f),
    )

    val fullPath = LinePath(path, true)
    val followPathBehavior = FollowPath(this, fullPath, 0.1f).apply {
        timeToTarget = 0.1f
        arrivalTolerance = 0.001f
        decelerationRadius = 90f.degRad
    }
    val lookBehavior = LookWhereYouAreGoing(this).apply {
        timeToTarget = 0.1f
        alignTolerance = 1f.degRad
        decelerationRadius = 90f.degRad
    }
    val behavior = BlendedSteering(this).apply {
        add(BlendedSteering.BehaviorAndWeight(followPathBehavior, 1f))
        add(BlendedSteering.BehaviorAndWeight(lookBehavior, 1f))
    }

    override fun getPosition() = position
    override fun getOrientation() = rotation.degRad
    override fun setOrientation(orientation: Float) { rotation = orientation.radDeg }
    override fun getLinearVelocity() = linearVelocity
    override fun getAngularVelocity() = angularVelocity
    fun setAngularVelocity(value: Float) { angularVelocity = value }
    override fun getBoundingRadius() = boundingRadius
    override fun getMaxLinearSpeed() = maxLinearSpeed
    override fun getMaxLinearAcceleration() = maxLinearAcceleration
    override fun getMaxAngularSpeed() = maxAngularSpeed
    override fun getMaxAngularAcceleration() = maxAngularAcceleration
    override fun isTagged() = tagged
    override fun setTagged(tagged: Boolean) { this.tagged = tagged }
    override fun vectorToAngle(vector: Vector2) = vector.toAngle()
    override fun angleToVector(outVector: Vector2, angle: Float) = angle.toVector(outVector)
}

class EnemyMovementPrototype {
    val enemy = Enemy(Vector2(0f, 0f), 0f)
    lateinit var onMoveCb: Enemy.() -> Unit

    fun onMove(callback: Enemy.() -> Unit) {
        onMoveCb = callback
    }

    fun tick(delta: Float) {
        val steeringOutput = SteeringAcceleration(Vector2())
        enemy.behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            enemy.applySteering(delta, steeringOutput)
            onMoveCb.invoke(enemy)
        }
    }

    private fun Enemy.applySteering(time: Float, steeringOutput: SteeringAcceleration<Vector2>) {
        position.mulAdd(linearVelocity, time)
        linearVelocity.mulAdd(steeringOutput.linear, time).limit(maxLinearSpeed)
        rotation += angularVelocity.radDeg * time
        angularVelocity += steeringOutput.angular * time
    }
}
