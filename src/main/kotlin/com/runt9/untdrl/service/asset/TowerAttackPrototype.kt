package com.runt9.untdrl.service.asset

import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.behaviors.Pursue
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.radDeg
import com.runt9.untdrl.util.ext.toAngle
import com.runt9.untdrl.util.ext.toVector
import com.runt9.untdrl.util.ext.unTdRlLogger

class Tower(val initialPosition: Vector2, val initialRotation: Float) : SteerableAdapter<Vector2>() {
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

    lateinit var target: Steerable<Vector2>

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
}

class Projectile(val initialPosition: Vector2, val initialRotation: Float, val target: Steerable<Vector2>) : SteerableAdapter<Vector2>() {
    private val position = initialPosition.cpy()!!
    var rotation: Float = initialRotation
    private val linearVelocity = Vector2()
    private val maxLinearSpeed = 3f
    private val maxLinearAcceleration = maxLinearSpeed * 100f
    private val maxAngularSpeed = 10f
    private val maxAngularAcceleration = maxAngularSpeed * 10f
    private var angularVelocity = 0f
    private val boundingRadius = 0.05f
    private var tagged = true

    val pursue = Pursue(this, target)
    val look = LookWhereYouAreGoing(this).apply {
        timeToTarget = 0.1f
        alignTolerance = 1f.degRad
        decelerationRadius = 90f.degRad
    }

    val behavior = BlendedSteering(this).apply {
        add(BlendedSteering.BehaviorAndWeight(pursue, 1f))
        add(BlendedSteering.BehaviorAndWeight(look, 1f))
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

class TowerAttackPrototype {
    private val logger = unTdRlLogger()
    val tower = Tower(Vector2(10f, 5f), 0f)
    private lateinit var onMoveCb: Tower.() -> Unit
    private lateinit var onProjCb: Projectile.() -> Unit
    private lateinit var onProjMoveCb: Projectile.() -> Unit
    private lateinit var onProjDieCb: Projectile.() -> Unit
    private val projectiles = mutableListOf<Projectile>()

    fun onMove(callback: Tower.() -> Unit) {
        onMoveCb = callback
    }

    fun onProj(callback: Projectile.() -> Unit) {
        onProjCb = callback
    }

    fun onProjMove(callback: Projectile.() -> Unit) {
        onProjMoveCb = callback
    }

    fun onProjDie(callback: (Projectile) -> Unit) {
        onProjDieCb = callback
    }

    fun tick(delta: Float) {
        val steeringOutput = SteeringAcceleration(Vector2())

        tower.attackTimer.tick(delta)

        if (tower.position.dst(tower.behavior.target.position) > 6) {
            return
        }

        tower.behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            tower.applySteering(delta, steeringOutput)
            onMoveCb.invoke(tower)
        }

        if (tower.attackTimer.isReady && steeringOutput.isZero) {
            logger.info { "Tower attacks!" }
            val projectile = Projectile(tower.position, tower.rotation, tower.target)
            onProjCb.invoke(projectile)
            projectiles += projectile
            tower.attackTimer.reset(false)
        }

        val projectilesToRemove = mutableSetOf<Projectile>()
        projectiles.forEach {
            if (!tickProjectile(it, delta)) {
                projectilesToRemove += it
            }
        }

        projectiles -= projectilesToRemove
    }

    private fun tickProjectile(projectile: Projectile, delta: Float): Boolean {
        if (projectile.position.dst(projectile.target.position) <= 0.1f) {
            logger.info { "Removing projectile" }
            onProjDieCb.invoke(projectile)
            return false
        }

        val steeringOutput = SteeringAcceleration(Vector2())
        projectile.behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            projectile.applySteering(delta, steeringOutput)
            onProjMoveCb.invoke(projectile)
        }

        return true
    }

    private fun Tower.applySteering(time: Float, steeringOutput: SteeringAcceleration<Vector2>) {
        rotation += angularVelocity.radDeg * time
        angularVelocity += steeringOutput.angular * time
    }

    private fun Projectile.applySteering(time: Float, steeringOutput: SteeringAcceleration<Vector2>) {
        position.mulAdd(linearVelocity, time)
        linearVelocity.mulAdd(steeringOutput.linear, time).limit(maxLinearSpeed)
        rotation += angularVelocity.radDeg * time
        angularVelocity += steeringOutput.angular * time
    }
}
