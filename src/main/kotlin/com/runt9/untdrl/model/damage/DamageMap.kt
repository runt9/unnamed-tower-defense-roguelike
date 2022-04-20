package com.runt9.untdrl.model.damage

data class DamageMap(val type: DamageType, var pctOfBase: Float = 1f, var penetration: Float = 0f)
