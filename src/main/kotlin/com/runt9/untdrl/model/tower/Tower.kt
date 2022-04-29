package com.runt9.untdrl.model.tower

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.Attribute
import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.model.tower.intercept.InterceptorHook
import com.runt9.untdrl.model.tower.intercept.TowerInteraction
import com.runt9.untdrl.model.tower.intercept.TowerInterceptor
import com.runt9.untdrl.model.tower.proc.TowerProc
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationDefinition
import com.runt9.untdrl.service.towerAction.TowerAction
import com.runt9.untdrl.util.ext.BaseSteerable

private var idCounter = 0

class Tower(val definition: TowerDefinition, val texture: Texture) : BaseSteerable(Vector2.Zero, 0f) {
    val id = idCounter++

    override val linearSpeedLimit = 0f
    override val linearAccelerationLimit = 0f
    override val angularSpeedLimit = 8f
    override val angularAccelerationLimit = angularSpeedLimit * 50f
    override val boundingBoxRadius = 0.5f

    private var onChangeCb: (suspend Tower.() -> Unit)? = null

    lateinit var action: TowerAction

    var xp = 0
    var xpToLevel = 10
    var level = 1
    var maxCores = 1

    val attrBase = definition.attrs.mapValues { (type, def) -> def.baseValue }.toMutableMap()
    val attrs = definition.attrs.mapValues { (type, _) -> Attribute(type) }.toMutableMap()
    val attrMods = mutableListOf<AttributeModifier>()
    var damageTypes = copyDefinitionDamageTypes()
    var targetingMode = TargetingMode.FRONT

    val cores = mutableListOf<TowerCore>()
    var canSpecialize = true
    val specializations = definition.specializations
    var appliedSpecialization: TowerSpecializationDefinition? = null

    val localXpModifiers = mutableListOf<Float>()

    private val interceptors = mutableMapOf<InterceptorHook, MutableList<TowerInterceptor<TowerInteraction>>>()
    val procs = mutableListOf<TowerProc>()

    private fun copyDefinitionDamageTypes() = definition.damageTypes.map { DamageMap(it.type, it.pctOfBase, it.penetration) }

    fun onChange(onChangeCb: suspend Tower.() -> Unit) {
        this.onChangeCb = onChangeCb
    }

    @Suppress("UNCHECKED_CAST")
    fun addInterceptor(interceptor: TowerInterceptor<out TowerInteraction>) {
        interceptors.computeIfAbsent(interceptor.hook) { mutableListOf() } += interceptor as TowerInterceptor<TowerInteraction>
    }

    fun intercept(hook: InterceptorHook, interaction: TowerInteraction) {
        interceptors[hook]?.forEach { it.intercept(this, interaction) }
    }
}

fun Map<AttributeType, Attribute>.mapToFloats() = mapValues { (_, v) -> v() }
