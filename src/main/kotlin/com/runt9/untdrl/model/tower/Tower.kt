package com.runt9.untdrl.model.tower

import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.ui.BaseSteerable

class Tower(val texture: Texture, val projTexture: Texture, initialPosition: Vector2 = Vector2.Zero, initialRotation: Float = 0f, var isPlaced: Boolean = false) : BaseSteerable(initialPosition, initialRotation) {
    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 2f
    override val boundingBoxRadius = 0.5f

    private var target: Enemy? = null
    private lateinit var onProjCb: Projectile.() -> Unit

    fun onProj(onProjCb: Projectile.() -> Unit) {
        this.onProjCb = onProjCb
    }

    val attackTimer = Timer(2f)

    val behavior = Face(this).apply {
        timeToTarget = 0.1f
        alignTolerance = 5f.degRad
        decelerationRadius = 90f.degRad
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
