package com.runt9.untdrl.util.ext

import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2

abstract class LocationAdapter : Location<Vector2> {
    private val position: Vector2 = Vector2.Zero
    var rotation = 0f

    override fun getPosition() = position
    override fun getOrientation() = rotation.degRad
    override fun setOrientation(orientation: Float) { rotation = orientation.radDeg }
    override fun vectorToAngle(vector: Vector2) = vector.toAngle()
    override fun angleToVector(outVector: Vector2, angle: Float) = angle.toVector(outVector)

    override fun newLocation(): Location<Vector2> {
        TODO("Not yet implemented")
    }
}
