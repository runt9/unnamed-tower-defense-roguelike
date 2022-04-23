package com.runt9.untdrl.model.building.intercept

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.critChance
import com.runt9.untdrl.model.building.critMulti
import com.runt9.untdrl.model.building.damage
import com.runt9.untdrl.model.building.intercept.InterceptorHook.AFTER_DAMAGE
import com.runt9.untdrl.model.building.intercept.InterceptorHook.BEFORE_DAMAGE
import com.runt9.untdrl.model.building.intercept.InterceptorHook.ON_ATTACK
import com.runt9.untdrl.util.ext.displayInt
import com.runt9.untdrl.util.ext.displayMultiplier
import com.runt9.untdrl.util.ext.displayPercent

enum class InterceptorHook {
    ON_ATTACK,
    ON_HIT,
    BEFORE_DAMAGE,
    AFTER_DAMAGE,

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

fun onAttack(intercept: (Building, OnAttack) -> Unit) = intercept(ON_ATTACK, intercept)
fun beforeDamage(intercept: (Building, DamageRequest) -> Unit) = intercept(BEFORE_DAMAGE, intercept)
fun afterDamage(intercept: (Building, DamageResult) -> Unit) = intercept(AFTER_DAMAGE, intercept)
