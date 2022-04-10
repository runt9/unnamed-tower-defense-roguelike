package com.runt9.untdrl.util.ext.ui

import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.radDeg
import com.runt9.untdrl.util.ext.toAngle
import com.runt9.untdrl.util.ext.toVector

abstract class BaseSteerable(initialPosition: Vector2, initialRotation: Float) : SteerableAdapter<Vector2>() {
    private val position = initialPosition.cpy()!!
    var rotation: Float = initialRotation
    private val linearVelocity = Vector2()
    private var angularVelocity = 0f
    private var tagged = true

    abstract val linearSpeedLimit: Float
    abstract val linearAccelerationLimit: Float
    abstract val angularSpeedLimit: Float
    abstract val angularAccelerationLimit: Float
    abstract val boundingBoxRadius: Float

    private val onMoveCbs = mutableListOf<BaseSteerable.() -> Unit>()

    fun onMove(onMoveCb: BaseSteerable.() -> Unit) {
        onMoveCbs += onMoveCb
    }

    override fun getPosition() = position
    override fun getOrientation() = rotation.degRad
    override fun setOrientation(orientation: Float) { rotation = orientation.radDeg }
    override fun getLinearVelocity() = linearVelocity
    override fun getAngularVelocity() = angularVelocity
    override fun getBoundingRadius() = boundingBoxRadius
    override fun getMaxLinearSpeed() = linearSpeedLimit
    override fun getMaxLinearAcceleration() = linearAccelerationLimit
    override fun getMaxAngularSpeed() = angularSpeedLimit
    override fun getMaxAngularAcceleration() = angularAccelerationLimit
    override fun isTagged() = tagged
    override fun setTagged(tagged: Boolean) { this.tagged = tagged }
    override fun vectorToAngle(vector: Vector2) = vector.toAngle()
    override fun angleToVector(outVector: Vector2, angle: Float) = angle.toVector(outVector)

    fun applySteering(time: Float, steeringOutput: SteeringAcceleration<Vector2>) {
        position.mulAdd(linearVelocity, time)
        linearVelocity.mulAdd(steeringOutput.linear, time).limit(maxLinearSpeed)
        rotation += angularVelocity.radDeg * time
        angularVelocity += steeringOutput.angular * time
        onMoveCbs.forEach { it() }
    }
}
