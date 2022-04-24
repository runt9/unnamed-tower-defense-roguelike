package com.runt9.untdrl.model.building

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.Attribute
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.building.intercept.BuildingInteraction
import com.runt9.untdrl.model.building.intercept.BuildingInterceptor
import com.runt9.untdrl.model.building.intercept.InterceptorHook
import com.runt9.untdrl.model.building.proc.BuildingProc
import com.runt9.untdrl.model.building.upgrade.BuildingUpgradeDefinition
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.service.buildingAction.BuildingAction
import com.runt9.untdrl.util.ext.BaseSteerable

private var idCounter = 0

class Building(val definition: BuildingDefinition, val texture: Texture) : BaseSteerable(Vector2.Zero, 0f) {
    val id = idCounter++

    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 8f
    override val angularAccelerationLimit = angularSpeedLimit * 50f
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
    var damageTypes = copyDefinitionDamageTypes()
    var targetingMode = TargetingMode.FRONT

    val cores = mutableListOf<TowerCore>()
    val availableUpgrades = mutableListOf<BuildingUpgradeDefinition>()
    val selectableUpgrades = mutableListOf<BuildingUpgradeDefinition>()
    val appliedUpgrades = mutableListOf<BuildingUpgradeDefinition>()

    val localXpModifiers = mutableListOf<Float>()

    private val interceptors = mutableMapOf<InterceptorHook, MutableList<BuildingInterceptor<BuildingInteraction>>>()
    val procs = mutableListOf<BuildingProc>()

    private fun copyDefinitionDamageTypes() = definition.damageTypes.map { DamageMap(it.type, it.pctOfBase, it.penetration) }

    fun onChange(onChangeCb: suspend Building.() -> Unit) {
        this.onChangeCb = onChangeCb
    }

    @Suppress("UNCHECKED_CAST")
    fun addInterceptor(interceptor: BuildingInterceptor<out BuildingInteraction>) {
        interceptors.computeIfAbsent(interceptor.hook) { mutableListOf() } += interceptor as BuildingInterceptor<BuildingInteraction>
    }

    fun intercept(hook: InterceptorHook, interaction: BuildingInteraction) {
        interceptors[hook]?.forEach { it.intercept(this, interaction) }
    }
}

fun Map<AttributeType, Attribute>.mapToFloats() = mapValues { (_, v) -> v() }
