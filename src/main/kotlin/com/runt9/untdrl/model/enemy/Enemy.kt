package com.runt9.untdrl.model.enemy

import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering
import com.badlogic.gdx.ai.steer.behaviors.FollowPath
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing
import com.badlogic.gdx.ai.steer.utils.paths.LinePath
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.util.ext.degRad
import com.runt9.untdrl.util.ext.BaseSteerable
import ktx.collections.GdxArray

class Enemy(wave: Int, val texture: Texture, initialPosition: Vector2, initialRotation: Float, path: GdxArray<Vector2>) : BaseSteerable(initialPosition, initialRotation) {
    override val linearSpeedLimit = 1f
    override val linearAccelerationLimit = linearSpeedLimit * 100f
    override val angularSpeedLimit = 10f
    override val angularAccelerationLimit = angularSpeedLimit * 100f
    override val boundingBoxRadius = 0.25f

    var maxHp = 100f + (25f * wave)
    var currentHp = maxHp
    val xpOnDeath = wave

    val affectedByTowers = mutableSetOf<Tower>()

    private val fullPath = LinePath(path, true)
    private val followPathBehavior = FollowPath(this, fullPath, 0.1f)
    private val lookBehavior = LookWhereYouAreGoing(this).apply {
        timeToTarget = 0.01f
        alignTolerance = 0f.degRad
        decelerationRadius = 45f.degRad
    }

    val behavior = BlendedSteering(this).apply {
        add(BlendedSteering.BehaviorAndWeight(followPathBehavior, 1f))
        add(BlendedSteering.BehaviorAndWeight(lookBehavior, 1f))
    }

    fun takeDamage(source: Tower, damage: Float) {
        currentHp -= damage
        affectedByTowers += source
    }

    fun numNodesToHome() = fullPath.segments.size - (followPathBehavior.pathParam as LinePathParam).segmentIndex
}
