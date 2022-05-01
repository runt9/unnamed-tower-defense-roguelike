package com.runt9.untdrl.service.towerAction.subAction

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.intercept.InterceptorHook
import com.runt9.untdrl.model.tower.intercept.OnAttack
import com.runt9.untdrl.util.ext.Timer

class AttackSubAction(private val tower: Tower, initialTimer: Float, private val canAttack: () -> Boolean, private val attackAction: suspend () -> Unit) : TowerSubAction {
    val timer = Timer(initialTimer)

    override suspend fun act(delta: Float) {
        if (timer.isReady && canAttack()) {
            timer.reset()
            attackAction()
            tower.intercept(InterceptorHook.ON_ATTACK, OnAttack(tower))
        }
    }
}
