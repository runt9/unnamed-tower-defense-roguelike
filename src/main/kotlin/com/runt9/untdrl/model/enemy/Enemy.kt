package com.runt9.untdrl.model.enemy

import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.FollowPath
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.utils.paths.LinePath
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.radDeg
import com.runt9.untdrl.util.ext.toAngle
import com.runt9.untdrl.util.ext.toVector
import com.runt9.untdrl.util.ext.unTdRlLogger
import ktx.collections.GdxArray

class Enemy(val texture: Texture, initialPosition: Vector2, initialRotation: Float, val path: GdxArray<Vector2>) : SteerableAdapter<Vector2>() {
    private val logger = unTdRlLogger()
    private val position = initialPosition.cpy()!!
    var rotation: Float = initialRotation
    private val linearVelocity = Vector2()
    private val maxLinearSpeed = 1f
    private val maxLinearAcceleration = maxLinearSpeed * 100f
    private val maxAngularSpeed = 10f
    private val maxAngularAcceleration = maxAngularSpeed * 100f
    private var angularVelocity = 0f
    private val boundingRadius = 0.375f
    private var tagged = true
    private lateinit var onMoveCb: Enemy.() -> Unit

    fun onMove(onMoveCb: Enemy.() -> Unit) {
        this.onMoveCb = onMoveCb
    }

    init {
        logger.info { "Enemy created with path ${path.toList()}" }
    }

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

    fun applySteering(time: Float, steeringOutput: SteeringAcceleration<Vector2>) {
        position.mulAdd(linearVelocity, time)
        linearVelocity.mulAdd(steeringOutput.linear, time).limit(maxLinearSpeed)
        rotation += angularVelocity.radDeg * time
        angularVelocity += steeringOutput.angular * time
        onMoveCb()
    }
}
