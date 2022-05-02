package com.runt9.untdrl.service.towerAction

import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Projectile
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.action.ProjectileAttackActionDefinition
import com.runt9.untdrl.model.tower.attackTime
import com.runt9.untdrl.service.towerAction.subAction.AttackSubAction
import com.runt9.untdrl.service.towerAction.subAction.faceTarget
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class ProjectileAttackAction(
    private val definition: ProjectileAttackActionDefinition,
    private val tower: Tower,
    override val eventBus: EventBus
) : TowerAction {
    private val logger = unTdRlLogger()

    private val faceTarget = faceTarget(tower)
    private val attack = AttackSubAction(tower, tower.attackTime, faceTarget::canAttackTarget, this::spawnProjectiles)

    var pierce = definition.pierce
    var homing = definition.homing
    var speed = definition.speed
    var delayedHoming = definition.delayedHoming
    var anglePerProjectile = definition.anglePerProjectile

    override suspend fun act(delta: Float) {
        // Easy way to avoid another callback, just check this every tick, it's not expensive
        if (tower.attackTime != attack.timer.targetTime) {
            attack.timer.targetTime = tower.attackTime
        }

        attack.timer.tick(delta)
        faceTarget.act(delta)
        // TODO: Couple wonky things happening here. Looks like attack timer isn't getting reset, target isn't getting reset, and it's attacking a target that's out of range or dead?
        //  also won't fire horizontally to the right
        attack.act(delta)
    }

    private fun spawnProjectiles() {
        val projCount = tower.attrs[AttributeType.PROJECTILE_COUNT]?.invoke() ?: 1
        // TODO: This calculation is wrong, a projectile should shoot from center _only_ if there is an odd number of total projectiles
        repeat(projCount.toInt()) { i ->
            var degreesFromCenter = ((i + 1) / 2) * anglePerProjectile
            if (i % 2 == 0) degreesFromCenter *= -1
            val projectile = Projectile(tower, definition.projectileTexture, faceTarget.target!!, pierce, homing, degreesFromCenter, speed, delayedHoming)
            eventBus.enqueueEventSync(ProjectileSpawnedEvent(projectile))
        }
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveEnd() {
        attack.timer.reset(false)
    }
}
