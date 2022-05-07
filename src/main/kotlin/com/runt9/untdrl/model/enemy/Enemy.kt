package com.runt9.untdrl.model.enemy

import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.FollowPath
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.utils.paths.LinePath
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.enemy.definition.EnemyDefinition
import com.runt9.untdrl.model.enemy.status.Slow
import com.runt9.untdrl.model.enemy.status.StatusEffect
import com.runt9.untdrl.model.enemy.status.Stun
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.ext.BaseSteerable
import com.runt9.untdrl.util.ext.clamp
import com.runt9.untdrl.util.ext.degRad
import ktx.collections.GdxArray

private var idCounter = 0

class Enemy(val definition: EnemyDefinition, wave: Int, initialPosition: Vector2, initialRotation: Float, path: GdxArray<Vector2>) : BaseSteerable(initialPosition, initialRotation) {
    private val difficultyModifier = 1 + (path.size / 200f)
    val id = idCounter++

    val statusEffects = mutableListOf<StatusEffect<*>>()

    override val linearSpeedLimit: Float get() {
        if (statusEffects.any { it is Stun }) return 0f
        val slowPct = statusEffects.filterIsInstance<Slow>().map { it.slowPct }.sum()
        return (definition.baseSpeed * difficultyModifier) * (1 - slowPct)
    }
    override val linearAccelerationLimit get() = linearSpeedLimit * 100f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 100f
    override val boundingBoxRadius = 0.25f

    var maxHp = (definition.baseHp + (25f * wave)) * difficultyModifier
    var currentHp = maxHp
    val xpOnDeath = wave
    var isAlive = true

    private val polygon: Polygon = generatePolygonBounds()
    val bounds: Polygon get() {
        polygon.setPosition(position.x, position.y)
        polygon.rotation = rotation
        return polygon
    }

    val affectedByTowers = mutableSetOf<Tower>()

    val fullPath = LinePath(path, true)
    val followPathBehavior = FollowPath(this, fullPath, 0.1f)
    private val lookBehavior = LookWhereYouAreGoing(this).apply {
        timeToTarget = 0.01f
        alignTolerance = 0f.degRad
        decelerationRadius = 45f.degRad
    }
    var resistances = definition.resistances.toMap()
        private set

    val behavior = BlendedSteering(this).apply {
        add(BlendedSteering.BehaviorAndWeight(followPathBehavior, 1f))
        add(BlendedSteering.BehaviorAndWeight(lookBehavior, 1f))
    }

    lateinit var onHpChangeCb: Enemy.() -> Unit

    fun onHpChange(onHpChangeCb: Enemy.() -> Unit) {
        this.onHpChangeCb = onHpChangeCb
    }

    fun numNodesToHome() = fullPath.segments.size - (followPathBehavior.pathParam as LinePathParam).segmentIndex

    fun <T : StatusEffect<T>> addStatusEffect(effect: T) {
        effect.applyStrategy.apply(statusEffects, effect)
    }

    fun reduceAllResistances(reduction: Float) {
        resistances = resistances.mapValues { (it.value - reduction).clamp(0.1f, 1.9f) }
    }

    private fun generatePolygonBounds(): Polygon {
        val top = Vector2(0f, 0.25f)
        val bottomRight = Vector2(0.25f, -0.25f)
        val bottomCenter = Vector2(0f, -0.125f)
        val bottomLeft = Vector2(-0.25f, -0.25f)

        return Polygon(FloatArray(8).apply {
            this[0] = top.x
            this[1] = top.y
            this[2] = bottomRight.x
            this[3] = bottomRight.y
            this[4] = bottomCenter.x
            this[5] = bottomCenter.y
            this[6] = bottomLeft.x
            this[7] = bottomLeft.y
        })
    }
}
