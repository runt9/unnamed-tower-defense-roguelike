package com.runt9.untdrl.service.towerAction

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Projectile
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.attackTime
import com.runt9.untdrl.model.tower.buffEffect
import com.runt9.untdrl.model.tower.definition.PulseCannonActionDefinition
import com.runt9.untdrl.model.tower.proc.TowerProc
import com.runt9.untdrl.service.towerAction.subAction.AttackSubAction
import com.runt9.untdrl.service.towerAction.subAction.faceTarget
import com.runt9.untdrl.util.ext.Size
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class PulseCannonAction(
    private val definition: PulseCannonActionDefinition,
    private val tower: Tower,
    override val eventBus: EventBus
) : TowerAction {
    private val logger = unTdRlLogger()

    private val faceTarget = faceTarget(tower)
    private val attack = AttackSubAction(tower, tower.attackTime, faceTarget::canAttackTarget, this::spawnProjectile)

    // TODO: This might end up getting used by more things, be prepared to move it
    val resistLowerProc = object : TowerProc {
        override val chance: Float = 1f

        override fun applyToEnemy(tower: Tower, enemy: Enemy, finalDamage: Float) {
            val reduction = definition.resistanceReduction * (1 + tower.buffEffect)
            enemy.reduceAllResistances(reduction)
        }
    }

    override fun init() {
        super.init()
        tower.addProc(resistLowerProc)
    }

    override suspend fun act(delta: Float) {
        // Easy way to avoid another callback, just check this every tick, it's not expensive
        if (tower.attackTime != attack.timer.targetTime) {
            attack.timer.targetTime = tower.attackTime
        }

        attack.timer.tick(delta)
        faceTarget.act(delta)
        attack.act(delta)
    }

    private fun spawnProjectile() {
        val projectile = Projectile(tower, definition.texture, faceTarget.target!!, Int.MAX_VALUE, speed = 5f, size = Size(1.05f, 0.5f), boundingPolygon = generateBounds())
        eventBus.enqueueEventSync(ProjectileSpawnedEvent(projectile))
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveEnd() {
        attack.timer.reset(false)
    }

    private fun generateBounds(): Polygon {
        val topCenter = Vector2(0f, 0.25f)
        val topRight = Vector2(0.525f, 0.05f)
        val bottomRight = Vector2(0.2625f, -0.25f)
        val bottomCenter = Vector2(0f, -0.15f)
        val bottomLeft = Vector2(-0.2625f, -0.25f)
        val topLeft = Vector2(-0.525f, 0.05f)

        return Polygon(FloatArray(12).apply {
            this[0] = topCenter.x
            this[1] = topCenter.y
            this[2] = topRight.x
            this[3] = topRight.y
            this[4] = bottomRight.x
            this[5] = bottomRight.y
            this[6] = bottomCenter.x
            this[7] = bottomCenter.y
            this[8] = bottomLeft.x
            this[9] = bottomLeft.y
            this[10] = topLeft.x
            this[11] = topLeft.y
        })
    }
}
