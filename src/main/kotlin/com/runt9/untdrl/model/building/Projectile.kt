package com.runt9.untdrl.model.building

import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.ext.BaseSteerable
import com.runt9.untdrl.util.ext.LocationAdapter
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.toVector

private var idCounter = 0

class Projectile(val owner: Building, val texture: Texture, pierce: Int) : BaseSteerable(owner.position, owner.rotation) {
    val id = idCounter++
    override val linearSpeedLimit = 10f
    override val linearAccelerationLimit = maxLinearSpeed * 100f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 10f
    override val boundingBoxRadius = 0.125f

    private lateinit var onDieCb: suspend Projectile.() -> Unit

    var remainingPierces = pierce
    val maxTravelDistance = owner.range
    var travelDistance = 0f
    val collidedWith = mutableListOf<Enemy>()
    
    fun onDie(onDieCb: suspend Projectile.() -> Unit) {
        this.onDieCb = onDieCb
    }

    private val goToPosition = position.cpy().add(rotation.degRad.toVector(Vector2(0f, 0f)).scl(owner.range))
    private val seek = Seek(this, object : LocationAdapter() {
        override fun getPosition() = goToPosition
    })

    val behavior = seek

    suspend fun die() {
        onDieCb()
    }
}
