package com.runt9.untdrl.service.towerAction

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.damage
import com.runt9.untdrl.model.tower.definition.FlamethrowerActionDefinition
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.proc.burnProc
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.service.towerAction.subAction.AttackSubAction
import com.runt9.untdrl.service.towerAction.subAction.faceTarget
import com.runt9.untdrl.util.ext.angleToWithin
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus

class FlamethrowerAction(
    private val definition: FlamethrowerActionDefinition,
    private val tower: Tower,
    override val eventBus: EventBus,
    private val enemyService: EnemyService
) : TowerAction {
    private val logger = unTdRlLogger()
    private val tickTime = 0.25f

    private val faceTarget = faceTarget(tower)
    private val attack = AttackSubAction(tower, tickTime, faceTarget::canAttackTarget, this::processDamageTick)
    var angle = definition.angle
    val burnProc = burnProc(definition.burnChance, definition.burnDuration, pctOfBaseDamage = definition.burnPctOfBase)

    override fun init() {
        super.init()
        tower.addProc(burnProc)
    }

    override suspend fun act(delta: Float) {
        attack.timer.tick(delta)
        faceTarget.act(delta)
        attack.act(delta)
    }

    private suspend fun processDamageTick() {
        enemyService.enemiesInRange(tower.position, tower.range).filter { enemy -> tower.angleToWithin(enemy, angle / 2f) }.forEach { enemy ->
            enemyService.performTickDamage(DamageSource.TOWER, tower, enemy, tower.damage * tickTime, tower.damageTypes, true)
        }
    }
}
