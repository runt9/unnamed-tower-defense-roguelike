package com.runt9.untdrl.model.tower.intercept

import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.critChance
import com.runt9.untdrl.model.tower.critMulti
import com.runt9.untdrl.model.tower.damage
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.AFTER_DAMAGE_CALC
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.BEFORE_DAMAGE_CALC
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.BEFORE_RESISTS
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.ON_ATTACK
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

interface TowerInterceptor<I : TowerInteraction> {
    val hook: InterceptorHook

    fun intercept(tower: Tower, interaction: I)
}

fun <I : TowerInteraction> intercept(hook: InterceptorHook, intercept: (Tower, I) -> Unit) = object : TowerInterceptor<I> {
    override val hook = hook

    override fun intercept(tower: Tower, interaction: I) = intercept(tower, interaction)
}

interface TowerInteraction
data class OnAttack(val tower: Tower) : TowerInteraction
data class DamageRequest(private val tower: Tower) : TowerInteraction {
    private val baseDamage = tower.damage
    private val baseCrit = tower.critChance
    private val baseCritMulti = tower.critMulti

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

data class DamageResult(val baseDamage: Float, val totalMulti: Float) : TowerInteraction {
    val totalDamage get() = baseDamage * totalMulti

    override fun toString() =
        "[Base: ${baseDamage.displayInt()} | Multi: ${totalMulti.displayMultiplier()} | Total: ${totalDamage.displayInt()}]"
}

data class ResistanceRequest(private val damageTypes: List<DamageMap>, private val resistances: Map<DamageType, Float>, val damageResult: DamageResult) : TowerInteraction {
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

fun onAttack(intercept: (Tower, OnAttack) -> Unit) = intercept(ON_ATTACK, intercept)
fun beforeDamage(intercept: (Tower, DamageRequest) -> Unit) = intercept(BEFORE_DAMAGE_CALC, intercept)
fun afterDamage(intercept: (Tower, DamageResult) -> Unit) = intercept(AFTER_DAMAGE_CALC, intercept)
fun beforeResists(intercept: (Tower, ResistanceRequest) -> Unit) = intercept(BEFORE_RESISTS, intercept)
