package com.runt9.untdrl.model.tower.intercept

import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.critChance
import com.runt9.untdrl.model.tower.critMulti
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.AFTER_DAMAGE_CALC
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.BEFORE_DAMAGE_CALC
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.BEFORE_RESISTS
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.CRIT_CHECK
import com.runt9.untdrl.model.tower.intercept.InterceptorHook.ON_ATTACK
import com.runt9.untdrl.util.ext.clamp
import com.runt9.untdrl.util.ext.displayDecimal
import com.runt9.untdrl.util.ext.displayMultiplier
import com.runt9.untdrl.util.ext.displayPercent

enum class InterceptorHook {
    ON_ATTACK,
    ON_HIT,
    CRIT_CHECK,
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

data class CritRequest(private val tower: Tower) : TowerInteraction {
    private val baseCrit = tower.critChance
    private val baseCritMulti = tower.critMulti

    private var additionalCritChance = 0f
    private var additionalCritMulti = 0f

    val totalCritChance get() = baseCrit + additionalCritChance
    val totalCritMulti get() = baseCritMulti + additionalCritMulti

    fun addCritChance(chance: Float) { additionalCritChance += chance }
    fun addCritMulti(multi: Float) { additionalCritMulti += multi }

    override fun toString() = "[Total Crit: ${totalCritChance.displayPercent(1)} | Total Crit Multi: ${totalCritMulti.displayMultiplier()}]"
}

enum class DamageSource {
    TOWER, PROJECTILE, BURN, BLEED, POISON
}

data class DamageRequest(val source: DamageSource, private val baseDamage: Float, val damageMultiplier: Float = 1f) : TowerInteraction {
    private var additionalBaseDamage = 0f
    private var additionalDamageMultiplier = 1f

    val totalBaseDamage get() = baseDamage + additionalBaseDamage
    val totalDamageMulti get() = damageMultiplier * additionalDamageMultiplier

    fun addBaseDamage(damage: Float) { additionalBaseDamage += damage }
    fun addDamageMultiplier(multi: Float) { additionalDamageMultiplier += multi }

    override fun toString() =
        "[Total Base: ${totalBaseDamage.displayDecimal()} | Total Multi: ${totalDamageMulti.displayMultiplier()}]"
}

data class DamageResult(val baseDamage: Float, val totalMulti: Float) : TowerInteraction {
    val totalDamage get() = baseDamage * totalMulti

    override fun toString() =
        "[Base: ${baseDamage.displayDecimal()} | Multi: ${totalMulti.displayMultiplier()} | Total: ${totalDamage.displayDecimal()}]"
}

data class ResistanceRequest(
    val source: DamageSource,
    private val damageTypes: List<DamageMap>,
    private val resistances: Map<DamageType, Float>,
    val damageResult: DamageResult
) : TowerInteraction {
    private val additionalDamageTypes = mutableListOf<DamageMap>()
    private var globalPenetration = 0f
    private val specificPenetration = mutableMapOf<DamageType, Float>()

    val finalDamage by lazy {
        (damageTypes + additionalDamageTypes).map { dt ->
            val damage = damageResult.totalDamage * dt.pctOfBase
            val resistance = getResistance(dt.type, dt.penetration)
            return@map damage / resistance
        }.sum()
    }

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
fun critCheck(intercept: (Tower, CritRequest) -> Unit) = intercept(CRIT_CHECK, intercept)
fun beforeDamage(vararg filterSource: DamageSource = DamageSource.values(), intercept: (Tower, DamageRequest) -> Unit) =
    intercept<DamageRequest>(BEFORE_DAMAGE_CALC) { tower, request ->
        if (!filterSource.contains(request.source)) return@intercept
        intercept(tower, request)
    }
fun afterDamage(intercept: (Tower, DamageResult) -> Unit) = intercept(AFTER_DAMAGE_CALC, intercept)
fun beforeResists(vararg filterSource: DamageSource = DamageSource.values(), intercept: (Tower, ResistanceRequest) -> Unit) =
    intercept<ResistanceRequest>(BEFORE_RESISTS) { tower, request ->
        if (!filterSource.contains(request.source)) return@intercept
        intercept(tower, request)
    }
