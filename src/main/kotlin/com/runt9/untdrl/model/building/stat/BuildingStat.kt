package com.runt9.untdrl.model.building.stat

class BuildingStat {
    var value = 0f

    operator fun invoke() = value
    operator fun invoke(value: Float) {
        this.value = value
    }
}
