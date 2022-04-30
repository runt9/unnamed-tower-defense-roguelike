package com.runt9.untdrl.service.duringRun

import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.CritRequest
import com.runt9.untdrl.model.tower.intercept.DamageRequest
import com.runt9.untdrl.model.tower.intercept.DamageResult
import com.runt9.untdrl.model.tower.intercept.InterceptorHook
import com.runt9.untdrl.model.tower.intercept.ResistanceRequest
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus

class AttackService(
    private val eventBus: EventBus,
    registry: RunServiceRegistry,
    private val towerService: TowerService,
    private val randomizer: RandomizerService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()

    suspend fun attackEnemy(tower: Tower, enemy: Enemy) {
        if (!enemy.isAlive) return

        calculateDamage(tower, enemy)
        if (enemy.currentHp <= 0) {
            enemy.isAlive = false
            enemy.affectedByTowers.forEach { t -> towerService.gainXp(t, enemy.xpOnDeath) }
            eventBus.enqueueEvent(EnemyRemovedEvent(enemy))
        }
    }

    private fun calculateDamage(tower: Tower, enemy: Enemy) {
        var damageMultiplier = 1f

        if (tower.hasAttribute(AttributeType.CRIT_CHANCE)) {
            val critCheck = CritRequest(tower)
            tower.intercept(InterceptorHook.CRIT_CHECK, critCheck)
            val isCrit = randomizer.percentChance(critCheck.totalCritChance)
            if (isCrit) damageMultiplier = critCheck.totalCritMulti
        }

        val damageRequest = DamageRequest(tower, damageMultiplier)
        tower.intercept(InterceptorHook.BEFORE_DAMAGE_CALC, damageRequest)
        logger.debug { "Final Damage Request: $damageRequest" }
        val damageResult = rollForDamage(damageRequest)
        tower.intercept(InterceptorHook.AFTER_DAMAGE_CALC, damageResult)
        logger.debug { "Final Damage Result: $damageResult" }
        val resistanceRequest = ResistanceRequest(tower.damageTypes.toList(), enemy.resistances.toMap(), damageResult)
        tower.intercept(InterceptorHook.BEFORE_RESISTS, resistanceRequest)
        enemy.takeDamage(tower, resistanceRequest.finalDamage)

        tower.procs.filter { randomizer.percentChance(it.chance) }.forEach { proc -> proc.applyToEnemy(enemy) }
    }

    private fun rollForDamage(request: DamageRequest): DamageResult {
        var damageMulti = randomizer.rng.nextInt(90, 111).toFloat() / 100f
        damageMulti *= request.totalDamageMulti
        return DamageResult(request.totalBaseDamage, damageMulti)
    }
}
