package com.runt9.untdrl.model.enemy.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.enemy.Biome

interface EnemyDefinition {
    val name: String
    val biome: Biome
    val texture: UnitTexture
    val baseHp: Int
    val baseSpeed: Float
    val resistances: Map<DamageType, Float>

    class Builder {
        internal val resistances = mutableMapOf<DamageType, Float>()

        fun resist(type: DamageType, amt: Float) {
            resistances[type] = amt
        }
    }
}

fun enemy(
    name: String,
    biome: Biome,
    texture: UnitTexture,
    baseHp: Int,
    baseSpeed: Float,
    init: EnemyDefinition.Builder.() -> Unit
): EnemyDefinition {
    val builder = EnemyDefinition.Builder()
    builder.init()

    return object : EnemyDefinition {
        override val name = name
        override val biome = biome
        override val texture = texture
        override val baseHp = baseHp
        override val baseSpeed = baseSpeed
        override val resistances = builder.resistances
    }
}
