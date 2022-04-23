package com.runt9.untdrl.model.enemy

import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.FollowPath
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.utils.paths.LinePath
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.enemy.definition.EnemyDefinition
import com.runt9.untdrl.util.ext.BaseSteerable
import com.runt9.untdrl.util.ext.degRad
import ktx.collections.GdxArray

private var idCounter = 0

class Enemy(val definition: EnemyDefinition, wave: Int, initialPosition: Vector2, initialRotation: Float, path: GdxArray<Vector2>) : BaseSteerable(initialPosition, initialRotation) {
    private val difficultyModifier = 1 + (path.size / 200f)
    val id = idCounter++

    val statusEffects = mutableListOf<StatusEffect>()

    override val linearSpeedLimit: Float get() {
        // TODO: Determine if this is really how we wanna handle stun
        if (statusEffects.any { it is Stun }) return 0f
        return definition.baseSpeed * difficultyModifier
    }
    override val linearAccelerationLimit get() = linearSpeedLimit * 100f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 100f
    override val boundingBoxRadius = 0.25f

    var maxHp = (definition.baseHp + (25f * wave)) * difficultyModifier
    var currentHp = maxHp
    val xpOnDeath = wave
    var isAlive = true

    val affectedByBuildings = mutableSetOf<Building>()

    private val fullPath = LinePath(path, true)
    private val followPathBehavior = FollowPath(this, fullPath, 0.1f)
    private val lookBehavior = LookWhereYouAreGoing(this).apply {
        timeToTarget = 0.01f
        alignTolerance = 0f.degRad
        decelerationRadius = 45f.degRad
    }
    val resistances = definition.resistances.toMap()

    val behavior = BlendedSteering(this).apply {
        add(BlendedSteering.BehaviorAndWeight(followPathBehavior, 1f))
        add(BlendedSteering.BehaviorAndWeight(lookBehavior, 1f))
    }

    private lateinit var onHpChangeCb: Enemy.() -> Unit

    fun onHpChange(onHpChangeCb: Enemy.() -> Unit) {
        this.onHpChangeCb = onHpChangeCb
    }

    fun takeDamage(source: Building, damage: Float) {
        currentHp -= damage
        affectedByBuildings += source
        onHpChangeCb()
    }

    fun numNodesToHome() = fullPath.segments.size - (followPathBehavior.pathParam as LinePathParam).segmentIndex

    inline fun <reified T : StatusEffect> addStatusEffect(effect: T) {
        val existingEffect = statusEffects.find { it is T }
        if (existingEffect == null || effect.stacks) {
            statusEffects += effect
            return
        }

        if (effect.refreshes) {
            existingEffect.timer.targetTime = effect.duration
        }
    }
}
