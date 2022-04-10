package com.runt9.untdrl.model.tower

import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.radDeg
import com.runt9.untdrl.util.ext.toAngle
import com.runt9.untdrl.util.ext.toVector

class Tower(val texture: Texture, val projTexture: Texture, initialPosition: Vector2 = Vector2.Zero, initialRotation: Float = 0f, var isPlaced: Boolean = false) : SteerableAdapter<Vector2>() {
    private val position = initialPosition.cpy()!!
    var rotation: Float = initialRotation
    private val linearVelocity = Vector2()
    private val maxLinearSpeed = 0f
    private val maxLinearAcceleration = 0f
    private val maxAngularSpeed = 10f
    private val maxAngularAcceleration = maxAngularSpeed * 2f
    private var angularVelocity = 0f
    private val boundingRadius = 0.375f
    private var tagged = true
    private var target: Steerable<Vector2>? = null

    private lateinit var onRotateCb: Tower.() -> Unit
    private lateinit var onProjCb: Projectile.() -> Unit

    fun onRotate(onRotateCb: Tower.() -> Unit) {
        this.onRotateCb = onRotateCb
    }

    fun onProj(onProjCb: Projectile.() -> Unit) {
        this.onProjCb = onProjCb
    }

    val attackTimer = Timer(2f)

    val behavior = Face(this).apply {
        timeToTarget = 0.1f
        alignTolerance = 5f.degRad
        decelerationRadius = 90f.degRad
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
        rotation += angularVelocity.radDeg * time
        angularVelocity += steeringOutput.angular * time
        onRotateCb()
    }

    fun spawnProjectile(): Projectile {
        val projectile = Projectile(projTexture, position, rotation, target!!)
        onProjCb.invoke(projectile)
        return projectile
    }

    fun setTarget(target: Enemy) {
        this.target = target
        behavior.target = target
    }
}
