package com.runt9.untdrl.model.tower

import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.behaviors.Pursue
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.ui.BaseSteerable

class Projectile(val texture: Texture, val damage: Float, initialPosition: Vector2, initialRotation: Float, val target: Enemy) : BaseSteerable(initialPosition, initialRotation) {
    override val linearSpeedLimit = 3f
    override val linearAccelerationLimit = maxLinearSpeed * 100f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 10f
    override val boundingBoxRadius = 0.125f

    private lateinit var onDieCb: Projectile.() -> Unit
    
    fun onDie(onDieCb: Projectile.() -> Unit) {
        this.onDieCb = onDieCb
    }

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

    fun die() {
        onDieCb()
    }
}
