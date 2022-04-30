package com.runt9.untdrl.model.tower

import com.runt9.untdrl.util.ext.displayName

enum class TargetingMode {
    FRONT,
    BACK,
    STRONG,
    WEAK,
    FAST,
    SLOW,
    NEAR_DEATH,
    HEALTHIEST;

    override fun toString() = displayName()
}
