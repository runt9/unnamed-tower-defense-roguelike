package com.runt9.untdrl.model.building

import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.BaseSteerable

class Building(val definition: BuildingDefinition, val texture: Texture, private val projTexture: Texture) : BaseSteerable(Vector2.Zero, 0f) {
    private val maxLevel = 20

    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 2f
    override val boundingBoxRadius = 0.5f

    private var target: Enemy? = null
    private lateinit var onProjCb: Projectile.() -> Unit

    var attackTime = definition.attackTime
    var damage = definition.damage
    var range = definition.range
    val attackTimer = Timer(attackTime)

    var xp = 0
    var xpToLevel = 10
    var level = 1

    fun onProj(onProjCb: Projectile.() -> Unit) {
        this.onProjCb = onProjCb
    }

    val behavior = Face(this).apply {
        timeToTarget = 0.1f
        alignTolerance = 5f.degRad
        decelerationRadius = 90f.degRad
    }

    fun spawnProjectile(): Projectile {
        val projectile = Projectile(this, projTexture, damage, position, rotation, target!!)
        onProjCb.invoke(projectile)
        return projectile
    }

    fun setTarget(target: Enemy) {
        this.target = target
        behavior.target = target
    }

    fun gainXp(xp: Int) {
        if (level == maxLevel) {
            return
        }

        this.xp += xp
        if (this.xp >= xpToLevel) {
            level++
            this.xp = this.xp - xpToLevel
            xpToLevel *= 2

            // TODO: Real scaling?
            attackTime *= .9f
            attackTimer.targetTime = attackTime
            damage *= 1.1f

            if (level % 5 == 0) {
                range++
            }
        }
    }
}
