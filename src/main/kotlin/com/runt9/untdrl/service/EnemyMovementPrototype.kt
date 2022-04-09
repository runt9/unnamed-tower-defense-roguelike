package com.runt9.untdrl.service.asset

import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.FollowPath
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.utils.paths.LinePath
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.path.IndexedGridGraph
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
        Vector2(0.25f, 1.25f),
        Vector2(0.25f, 2.25f),
        Vector2(0.25f, 3.25f),
        Vector2(1.25f, 3.25f),
        Vector2(2.25f, 3.25f),
        Vector2(3.25f, 3.25f),
        Vector2(4.25f, 3.25f),
        Vector2(4.25f, 4.25f),
        Vector2(5.25f, 4.25f),
        Vector2(6.25f, 4.25f),
        Vector2(7.25f, 4.25f),
        Vector2(7.25f, 5.25f),
        Vector2(7.25f, 6.25f),
        Vector2(7.25f, 7.25f),
    )

    val fullPath = LinePath(path, true)
    val followPathBehavior = FollowPath(this, fullPath, 0.1f)
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
    val enemy = Enemy(Vector2(0.25f, 0.75f), 0f)
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

    fun findPath(spawner: Vector2, grid: IndexedGridGraph) {
    }
}
