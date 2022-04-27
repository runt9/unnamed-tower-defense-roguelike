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

    // TODO: This is kinda hacky but I'd need to override LibGDX SelectBox to change how the toString is called on an item
    override fun toString() = displayName()
}
