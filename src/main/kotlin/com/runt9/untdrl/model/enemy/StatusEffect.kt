package com.runt9.untdrl.model.enemy

import com.runt9.untdrl.util.ext.Timer

abstract class StatusEffect(val duration: Float, val stacks: Boolean, val refreshes: Boolean) {
    val timer = Timer(duration)

    fun tick(delta: Float) {
        timer.tick(delta)
    }
}

class Stun(duration: Float) : StatusEffect(duration, false, false)
