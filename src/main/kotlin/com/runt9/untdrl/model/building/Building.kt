package com.runt9.untdrl.model.building

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.service.buildingAction.BuildingAction
import com.runt9.untdrl.util.ext.BaseSteerable
import kotlin.math.roundToInt

private var idCounter = 0

class Building(val definition: BuildingDefinition, val texture: Texture) : BaseSteerable(Vector2.Zero, 0f) {
    private val maxLevel = 20
    val id = idCounter++

    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 15f
    override val angularAccelerationLimit = angularSpeedLimit * 2f
    override val boundingBoxRadius = 0.5f

    private var onChangeCb: (Building.() -> Unit)? = null

    lateinit var action: BuildingAction

    var xp = 0
    var xpToLevel = 10
    var level = 1
    var maxCores = 1

    val cores = mutableListOf<TowerCore>()

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
            if (level != maxLevel) {
                this.xp = this.xp - xpToLevel
                xpToLevel = (xpToLevel * 1.5f).roundToInt()
            }
            action.levelUp(level)
        }

        onChangeCb?.invoke(this)
    }
}
