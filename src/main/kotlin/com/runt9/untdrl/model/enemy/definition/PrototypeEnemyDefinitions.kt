package com.runt9.untdrl.model.enemy.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.damage.DamageType.EARTH
import com.runt9.untdrl.model.damage.DamageType.FIRE
import com.runt9.untdrl.model.damage.DamageType.ICE
import com.runt9.untdrl.model.damage.DamageType.LIGHTNING
import com.runt9.untdrl.model.damage.DamageType.PHYSICAL
import com.runt9.untdrl.model.damage.DamageType.WATER
import com.runt9.untdrl.model.enemy.Biome.CLOUD
import com.runt9.untdrl.model.enemy.Biome.DESERT
import com.runt9.untdrl.model.enemy.Biome.GRASSLAND
import com.runt9.untdrl.model.enemy.Biome.TUNDRA
import com.runt9.untdrl.model.enemy.Biome.VOLCANO

val desertEnemy = enemy("Desert Enemy", DESERT, UnitTexture.ENEMY, 125, 0.8f) {
    resist(PHYSICAL, 1.5f)
    resist(FIRE, 1.25f)
    resist(WATER, 0.5f)
    resist(EARTH, 0.75f)
}

val volcanoEnemy = enemy("Volcano Enemy", VOLCANO, UnitTexture.ENEMY, 150, 0.65f) {
    resist(PHYSICAL, 1.25f)
    resist(FIRE, 1.75f)
    resist(ICE, 1.5f)
    resist(WATER, 0.25f)
    resist(EARTH, 0.75f)
}

val grasslandEnemy = enemy("Grassland Enemy", GRASSLAND, UnitTexture.ENEMY, 90, 1.25f) {
    resist(FIRE, 0.5f)
    resist(ICE, 0.75f)
    resist(WATER, 1.25f)
    resist(EARTH, 1.25f)
    resist(LIGHTNING, 1.25f)
}

val tundraEnemy = enemy("Tundra Enemy", TUNDRA, UnitTexture.ENEMY, 75, 1.1f) {
    resist(PHYSICAL, 0.75f)
    resist(FIRE, 0.25f)
    resist(ICE, 1.75f)
    resist(WATER, 1.25f)
    resist(EARTH, 1.5f)
    resist(LIGHTNING, 1.25f)
}

val cloudEnemy = enemy("Cloud Enemy", CLOUD, UnitTexture.ENEMY, 50, 1.5f) {
    resist(PHYSICAL, 1.5f)
    resist(FIRE, 0.75f)
    resist(ICE, 0.75f)
    resist(WATER, 1.25f)
    resist(EARTH, 1.5f)
    resist(LIGHTNING, 0.25f)
}

val possibleEnemies = mapOf(
    DESERT to listOf(desertEnemy),
    VOLCANO to listOf(volcanoEnemy),
    GRASSLAND to listOf(grasslandEnemy),
    TUNDRA to listOf(tundraEnemy),
    CLOUD to listOf(cloudEnemy)
)
