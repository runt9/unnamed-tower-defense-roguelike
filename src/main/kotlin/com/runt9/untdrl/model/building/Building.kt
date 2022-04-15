package com.runt9.untdrl.model.building

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.service.buildingAction.BuildingAction
import com.runt9.untdrl.util.ext.BaseSteerable

class Building(val definition: BuildingDefinition, val texture: Texture) : BaseSteerable(Vector2.Zero, 0f) {
    private val maxLevel = 20

    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 2f
    override val boundingBoxRadius = 0.5f

    private var onChangeCb: (Building.() -> Unit)? = null

    lateinit var action: BuildingAction

    var xp = 0
    var xpToLevel = 10
    var level = 1

    fun onChange(onChangeCb: Building.() -> Unit) {
        this.onChangeCb = onChangeCb
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
            action.levelUp(level)
        }

        onChangeCb?.invoke(this)
    }
}
