package com.runt9.untdrl.model.building

import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.behaviors.Pursue
import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.ext.BaseSteerable
import com.runt9.untdrl.util.ext.degRad

private var idCounter = 0

class Projectile(val owner: Building, val texture: Texture, val damage: Float, val target: Enemy) : BaseSteerable(owner.position, owner.rotation) {
    val id = idCounter++
    override val linearSpeedLimit = 3f
    override val linearAccelerationLimit = maxLinearSpeed * 100f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 10f
    override val boundingBoxRadius = 0.125f

    private lateinit var onDieCb: suspend Projectile.() -> Unit
    
    fun onDie(onDieCb: suspend Projectile.() -> Unit) {
        this.onDieCb = onDieCb
    }

    private val pursue = Pursue(this, target)
    private val look = LookWhereYouAreGoing(this).apply {
        timeToTarget = 0.1f
        alignTolerance = 1f.degRad
        decelerationRadius = 90f.degRad
    }

    val behavior = BlendedSteering(this).apply {
        add(BlendedSteering.BehaviorAndWeight(pursue, 1f))
        add(BlendedSteering.BehaviorAndWeight(look, 1f))
    }

    suspend fun die() {
        onDieCb()
    }
}
