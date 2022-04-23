package com.runt9.untdrl.model.building.intercept

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.critChance
import com.runt9.untdrl.model.building.critMulti
import com.runt9.untdrl.model.building.damage
import com.runt9.untdrl.model.building.intercept.InterceptorHook.AFTER_DAMAGE_CALC
import com.runt9.untdrl.model.building.intercept.InterceptorHook.BEFORE_DAMAGE_CALC
import com.runt9.untdrl.model.building.intercept.InterceptorHook.BEFORE_RESISTS
import com.runt9.untdrl.model.building.intercept.InterceptorHook.ON_ATTACK
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.util.ext.clamp
import com.runt9.untdrl.util.ext.displayInt
import com.runt9.untdrl.util.ext.displayMultiplier
import com.runt9.untdrl.util.ext.displayPercent

enum class InterceptorHook {
    ON_ATTACK,
    ON_HIT,
    BEFORE_DAMAGE_CALC,
    AFTER_DAMAGE_CALC,
    BEFORE_RESISTS,
    AFTER_DAMAGE_DEALT,

    BEFORE_GENERATE_GOLD,
    AFTER_GENERATE_GOLD,

    BEFORE_GENERATE_RESEARCH,
    AFTER_GENERATE_RESEARCH
}

interface BuildingInterceptor<I : BuildingInteraction> {
    val hook: InterceptorHook

    fun intercept(building: Building, interaction: I)
}

fun <I : BuildingInteraction> intercept(hook: InterceptorHook, intercept: (Building, I) -> Unit) = object : BuildingInterceptor<I> {
    override val hook = hook

    override fun intercept(building: Building, interaction: I) = intercept(building, interaction)
}

interface BuildingInteraction
data class OnAttack(val building: Building) : BuildingInteraction
data class DamageRequest(private val building: Building) : BuildingInteraction {
    private val baseDamage = building.damage
    private val baseCrit = building.critChance
    private val baseCritMulti = building.critMulti

    private var additionalBaseDamage = 0f
    private var additionalDamageMultiplier = 1f
    private var additionalCritChance = 0f
    private var additionalCritMulti = 0f

    val totalBaseDamage get() = baseDamage + additionalBaseDamage
    val totalCritChance get() = baseCrit + additionalCritChance
    val totalCritMulti get() = baseCritMulti + additionalCritMulti
    val totalDamageMulti get() = additionalDamageMultiplier

    fun addBaseDamage(damage: Float) { additionalBaseDamage += damage }
    fun addDamageMultiplier(multi: Float) { additionalDamageMultiplier += multi }
    fun addCritChance(chance: Float) { additionalCritChance += chance }
    fun addCritMulti(multi: Float) { additionalCritMulti += multi }

    override fun toString() =
        "[Total Base: ${totalBaseDamage.displayInt()} | Total Crit: ${(totalCritChance * 100f).displayPercent(1)} | Total Crit Multi: ${totalCritMulti.displayMultiplier()} | Total Multi: ${totalDamageMulti.displayMultiplier()}]"
}

data class DamageResult(val baseDamage: Float, val totalMulti: Float) : BuildingInteraction {
    val totalDamage get() = baseDamage * totalMulti

    override fun toString() =
        "[Base: ${baseDamage.displayInt()} | Multi: ${totalMulti.displayMultiplier()} | Total: ${totalDamage.displayInt()}]"
}

data class ResistanceRequest(private val damageTypes: List<DamageMap>, private val resistances: Map<DamageType, Float>, val damageResult: DamageResult) : BuildingInteraction {
    private val additionalDamageTypes = mutableListOf<DamageMap>()
    private var globalPenetration = 0f
    private val specificPenetration = mutableMapOf<DamageType, Float>()

    val finalDamage get() = (damageTypes + additionalDamageTypes).map { dt ->
        val damage = damageResult.totalDamage * dt.pctOfBase
        val resistance = getResistance(dt.type, dt.penetration)
        return@map damage / resistance
    }.sum()

    private fun getResistance(type: DamageType, penetration: Float): Float {
        val resist = resistances.getOrDefault(type, 1f)
        // Resists are capped from -90% to +90%
        return (resist - penetration - globalPenetration).clamp(0.1f, 1.9f)
    }

    fun addDamageType(type: DamageType, pctOfBase: Float = 1f, penetration: Float = 0f) {
        additionalDamageTypes += DamageMap(type, pctOfBase, penetration)
    }

    fun addGlobalPenetration(pen: Float) {
        globalPenetration += pen
    }

    fun addPenetration(type: DamageType, pen: Float) = specificPenetration.merge(type, pen) { old, value -> old + value }
}

fun onAttack(intercept: (Building, OnAttack) -> Unit) = intercept(ON_ATTACK, intercept)
fun beforeDamage(intercept: (Building, DamageRequest) -> Unit) = intercept(BEFORE_DAMAGE_CALC, intercept)
fun afterDamage(intercept: (Building, DamageResult) -> Unit) = intercept(AFTER_DAMAGE_CALC, intercept)
fun beforeResists(intercept: (Building, ResistanceRequest) -> Unit) = intercept(BEFORE_RESISTS, intercept)
