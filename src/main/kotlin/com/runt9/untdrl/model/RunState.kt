package com.runt9.untdrl.model

import com.runt9.untdrl.util.ext.randomString
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class RunState(
    val seed: String = Random.randomString(8),
    var gold: Int = 0,
    var wave: Int = 1,
    var waveActive: Boolean = false
)
