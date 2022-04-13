package com.runt9.untdrl.model.tower

import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.ui.BaseSteerable

class Tower(val definition: TowerDefinition, val texture: Texture, private val projTexture: Texture) : BaseSteerable(Vector2.Zero, 0f) {
    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 2f
    override val boundingBoxRadius = 0.5f

    private var target: Enemy? = null
    private lateinit var onProjCb: Projectile.() -> Unit

    var isPlaced: Boolean = false
    val attackTimer = Timer(definition.attackTime)
    var damage = definition.damage
    var range = definition.range

    fun onProj(onProjCb: Projectile.() -> Unit) {
        this.onProjCb = onProjCb
    }

    val behavior = Face(this).apply {
        timeToTarget = 0.1f
        alignTolerance = 5f.degRad
        decelerationRadius = 90f.degRad
    }

    fun spawnProjectile(): Projectile {
        val projectile = Projectile(projTexture, damage, position, rotation, target!!)
        onProjCb.invoke(projectile)
        return projectile
    }

    fun setTarget(target: Enemy) {
        this.target = target
        behavior.target = target
    }
}
