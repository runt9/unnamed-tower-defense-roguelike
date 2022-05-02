package com.runt9.untdrl.model.tower

import com.badlogic.gdx.ai.steer.behaviors.Arrive
import com.runt9.untdrl.model.TextureDefinition
import com.runt9.untdrl.util.ext.BaseSteerable
import com.runt9.untdrl.util.ext.LocationAdapter

class Mine(
    val owner: Tower,
    val texture: TextureDefinition,
    val landingSpot: LocationAdapter
) : BaseSteerable(owner.position, owner.rotation) {
    override val linearSpeedLimit = 4f
    override val linearAccelerationLimit = maxLinearSpeed * 100f
    override val angularSpeedLimit = 5f
    override val angularAccelerationLimit = angularSpeedLimit * 10f
    override val boundingBoxRadius = 0.125f

    private lateinit var onDieCb: suspend Mine.() -> Unit
    // A mine is not armed until it has reached its target position
    var armed = false

    fun onDie(onDieCb: suspend Mine.() -> Unit) {
        this.onDieCb = onDieCb
    }

    var behavior = Arrive(this, landingSpot)

    suspend fun die() {
        onDieCb()
    }
}
