package com.runt9.untdrl.service.towerAction

import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.attackTime
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.service.towerAction.subAction.AttackSubAction
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class PointBlankSpecializationAction(
    private val tower: Tower,
    override val eventBus: EventBus,
    private val enemyService: EnemyService
) : TowerAction {
    private val logger = unTdRlLogger()

    private val attack = AttackSubAction(tower, tower.attackTime, { true }, this::performPulse)

    override suspend fun act(delta: Float) {
        // Easy way to avoid another callback, just check this every tick, it's not expensive
        if (tower.attackTime != attack.timer.targetTime) {
            attack.timer.targetTime = tower.attackTime
        }

        attack.timer.tick(delta)
        attack.act(delta)
    }

    private suspend fun performPulse() {
        enemyService.enemiesInRange(tower.position, tower.range).forEach { enemy ->
            enemyService.attackEnemy(DamageSource.TOWER, tower, enemy, enemy.position)
        }
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveEnd() {
        attack.timer.reset(false)
    }
}
