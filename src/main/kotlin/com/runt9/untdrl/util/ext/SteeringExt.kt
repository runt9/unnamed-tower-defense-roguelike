package com.runt9.untdrl.util.ext

import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.utils.ArithmeticUtils
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun Vector2.toAngle() = atan2(-x.toDouble(), y.toDouble()).toFloat()

fun Float.toVector(outVector: Vector2): Vector2 {
    outVector.x = (-sin(toDouble())).toFloat()
    outVector.y = cos(toDouble()).toFloat()
    return outVector
}

fun BaseSteerable.angleTo(other: Steerable<Vector2>) = angleTo(other.position)
fun BaseSteerable.angleTo(otherPos: Vector2) = otherPos.cpy().sub(position.cpy()).nor().angleDeg() - 90f
fun BaseSteerable.angleToWithin(other: Steerable<Vector2>, limit: Float) = angleToWithin(other.position, limit)
fun BaseSteerable.angleToWithin(otherPos: Vector2, limit: Float) = abs(ArithmeticUtils.wrapAngleAroundZero(rotation.degRad).radDeg - angleTo(otherPos)) <= limit
