package com.runt9.untdrl.model.building

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.Attribute
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.building.upgrade.BuildingUpgrade
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.service.buildingAction.BuildingAction
import com.runt9.untdrl.util.ext.BaseSteerable

private var idCounter = 0

class Building(val definition: BuildingDefinition, val texture: Texture) : BaseSteerable(Vector2.Zero, 0f) {
    val id = idCounter++

    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 15f
    override val angularAccelerationLimit = angularSpeedLimit * 2f
    override val boundingBoxRadius = 0.5f

    private var onChangeCb: (suspend Building.() -> Unit)? = null

    lateinit var action: BuildingAction

    var xp = 0
    var xpToLevel = 10
    var level = 1
    var maxCores = 1
    var upgradePoints = 0
    var selectableUpgradeOptions = 2

    val attrs = definition.attrs.mapValues { (type, _) -> Attribute(type) }.toMutableMap()
    val attrMods = mutableListOf<AttributeModifier>()
    val damageTypes = copyDefinitionDamageTypes()

    private fun copyDefinitionDamageTypes() = definition.damageTypes.map { DamageMap(it.type, it.pctOfBase, it.penetration) }

    val cores = mutableListOf<TowerCore>()
    val availableUpgrades = mutableListOf<BuildingUpgrade>()
    val selectableUpgrades = mutableListOf<BuildingUpgrade>()
    val appliedUpgrades = mutableListOf<BuildingUpgrade>()

    fun onChange(onChangeCb: suspend Building.() -> Unit) {
        this.onChangeCb = onChangeCb
    }
}

fun Map<AttributeType, Attribute>.mapToFloats() = mapValues { (_, v) -> v() }
