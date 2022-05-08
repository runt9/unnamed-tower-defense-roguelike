package com.runt9.untdrl.model.tower

import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.Attribute
import com.runt9.untdrl.model.attribute.AttributeModificationType
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
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationEffectDefinition
import com.runt9.untdrl.service.specializationEffect.TowerSpecializationEffect
import com.runt9.untdrl.service.towerAction.TowerAction
import com.runt9.untdrl.util.ext.BaseSteerable
import kotlin.math.max

private var idCounter = 0

class Tower(val definition: TowerDefinition) : BaseSteerable(Vector2.Zero.cpy(), 0f) {
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

    val attrBase = definition.attrs.mapValues { (_, def) -> def.baseValue }.toMutableMap()
    val attrGrowth = definition.attrs.mapValues { (_, def) -> Pair(def.growthType, def.growthPerLevel) }.toMutableMap()
    val attrs = definition.attrs.mapValues { (type, _) -> Attribute(type) }.toMutableMap()
    val attrMods = mutableListOf<AttributeModifier>()
    val damageTypes = copyDefinitionDamageTypes().toMutableList()
    var canChangeTargetingMode = definition.canChangeTargetingMode
    var targetingMode = TargetingMode.FRONT

    val cores = mutableListOf<TowerCore>()
    var canSpecialize = true
    val specializations = definition.specializations
    var appliedSpecialization: TowerSpecializationDefinition? = null
    var appliedSpecializationEffect: TowerSpecializationEffect? = null

    val localXpModifiers = mutableListOf<Float>()
    val affectedByTowers = mutableSetOf<Tower>()

    private val interceptors = mutableMapOf<InterceptorHook, MutableList<TowerInterceptor<TowerInteraction>>>()
    val procs = mutableListOf<TowerProc>()

    private fun copyDefinitionDamageTypes() = definition.damageTypes.map { DamageMap(it.type, it.pctOfBase, it.penetration) }

    fun onChange(onChangeCb: suspend Tower.() -> Unit) {
        this.onChangeCb = onChangeCb
    }

    fun addProc(proc: TowerProc) {
        procs += proc
    }

    @Suppress("UNCHECKED_CAST")
    fun addInterceptor(interceptor: TowerInterceptor<out TowerInteraction>) {
        interceptors.computeIfAbsent(interceptor.hook) { mutableListOf() } += interceptor as TowerInterceptor<TowerInteraction>
    }

    fun intercept(hook: InterceptorHook, interaction: TowerInteraction) {
        interceptors[hook]?.forEach { it.intercept(this, interaction) }
    }

    fun hasAttribute(attr: AttributeType) = attrs.containsKey(attr)

    fun modifyBase(attr: AttributeType, flatModifier: Float = 0f, percentModifier: Float = 0f) {
        val base = attrBase[attr] ?: 0f
        val newValue = ((base + flatModifier) * (1 + (percentModifier / 100)))
        attrBase[attr] = max(0f, newValue)
    }

    fun modifyLevelGrowth(attr: AttributeType, flatModifier: Float = 0f, percentModifier: Float = 0f) {
        val growth = attrGrowth[attr] ?: Pair(AttributeModificationType.FLAT, 0f)
        val newValue = ((growth.second + flatModifier) * (1 + (percentModifier / 100)))
        attrGrowth[attr] = Pair(growth.first, max(0f, newValue))
    }

    fun modifyBaseAndLevelGrowth(attr: AttributeType, flatModifier: Float = 0f, percentModifier: Float = 0f) {
        modifyBase(attr, flatModifier, percentModifier)
        modifyLevelGrowth(attr, flatModifier, percentModifier)
    }

    fun inRangeOf(pos: Vector2) = position.dst(pos) <= range

    fun modifyAllAttributes(flatModifier: Float = 0f, percentModifier: Float = 0f, isTemporary: Boolean = false) {
        attrs.keys.forEach { type ->
            attrMods += AttributeModifier(type, flatModifier, percentModifier, isTemporary)
        }
    }

    inline fun <reified T : TowerSpecializationEffectDefinition> isSpecialization() = appliedSpecialization?.effect is T
}

fun Map<AttributeType, Attribute>.mapToFloats() = mapValues { (_, v) -> v() }
