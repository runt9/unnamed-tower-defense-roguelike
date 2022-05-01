package com.runt9.untdrl.service.towerAction

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Face
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.action.FlamethrowerActionDefinition
import com.runt9.untdrl.model.tower.damage
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.intercept.InterceptorHook
import com.runt9.untdrl.model.tower.intercept.OnAttack
import com.runt9.untdrl.model.tower.proc.burnProc
import com.runt9.untdrl.model.tower.range
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.ext.angleToWithin
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus

// TODO: Once more towers start getting created, figure out how we want to abstract away common stuff
class FlamethrowerAction(
    private val definition: FlamethrowerActionDefinition,
    private val tower: Tower,
    override val eventBus: EventBus,
    private val enemyService: EnemyService
) : TowerAction {
    private val logger = unTdRlLogger()
    private var target: Enemy? = null

    private val tickTime = 0.25f

    private val tickTimer = Timer(tickTime)
    private val behavior = Face(tower).apply {
        timeToTarget = 0.01f
        alignTolerance = 1f.degRad
        decelerationRadius = 45f.degRad
    }
    var angle = definition.angle
    val burnProc = burnProc(definition.burnChance, definition.burnDuration, pctOfBaseDamage = definition.burnPctOfBase)

    override fun init() {
        super.init()
        tower.procs += burnProc
    }

    override suspend fun act(delta: Float) {
        val steeringOutput = SteeringAcceleration(Vector2())

        // TODO: Determine if doing damage calculations every tick is worth the "smoothness" or if we want to limit it to a certain number of ticks per second
        //  Could also just go the route of having a "proc timer" that limits procs from firing more than X times per second

        tickTimer.tick(delta)

        val target = enemyService.getTowerTarget(tower.position, tower.range, tower.targetingMode) ?: return

        setTarget(target)

        behavior.calculateSteering(steeringOutput)
        if (!steeringOutput.isZero) {
            tower.applySteering(delta, steeringOutput)
        }

        if (tickTimer.isReady && tower.angleToWithin(target, 3f)) {
            tickTimer.reset()
            processDamageTick()
            tower.intercept(InterceptorHook.ON_ATTACK, OnAttack(tower))
        }
    }

    private suspend fun processDamageTick() {
        enemyService.enemiesInRange(tower.position, tower.range).filter { enemy -> tower.angleToWithin(enemy, angle / 2f) }.forEach { enemy ->
            enemyService.performTickDamage(DamageSource.TOWER, tower, enemy, tower.damage * tickTime, tower.damageTypes, true)
        }
    }

    // TODO: Target switching every frame seems really awful
    private fun setTarget(target: Enemy) {
        this.target = target
        behavior.target = target
    }
}
