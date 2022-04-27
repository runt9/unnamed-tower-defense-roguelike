package com.runt9.untdrl.model.enemy.definition

import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.damage.DamageType.COLD
import com.runt9.untdrl.model.damage.DamageType.ENERGY
import com.runt9.untdrl.model.damage.DamageType.HEAT
import com.runt9.untdrl.model.damage.DamageType.MYSTIC
import com.runt9.untdrl.model.damage.DamageType.NATURE
import com.runt9.untdrl.model.damage.DamageType.PHYSICAL
import com.runt9.untdrl.model.enemy.Biome.CLOUD
import com.runt9.untdrl.model.enemy.Biome.CORRUPTED
import com.runt9.untdrl.model.enemy.Biome.DESERT
import com.runt9.untdrl.model.enemy.Biome.ENCHANTED
import com.runt9.untdrl.model.enemy.Biome.FOREST
import com.runt9.untdrl.model.enemy.Biome.GRASSLAND
import com.runt9.untdrl.model.enemy.Biome.LAVA
import com.runt9.untdrl.model.enemy.Biome.MOUNTAIN
import com.runt9.untdrl.model.enemy.Biome.TECH
import com.runt9.untdrl.model.enemy.Biome.TUNDRA

val grasslandEnemy = enemy("Grassland Enemy", GRASSLAND, UnitTexture.ENEMY, 90, 1.1f) {
    resists(NATURE, COLD)
    weakTo(HEAT, ENERGY, MYSTIC)
}

val forestEnemy = enemy("Forest Enemy", FOREST, UnitTexture.ENEMY, 100, 0.75f) {
    resists(NATURE, MYSTIC)
    weakTo(PHYSICAL, HEAT)
}

val desertEnemy = enemy("Desert Enemy", DESERT, UnitTexture.ENEMY, 115, 0.8f) {
    resists(PHYSICAL, HEAT, ENERGY)
    weakTo(COLD)
}

val lavaEnemy = enemy("Volcano Enemy", LAVA, UnitTexture.ENEMY, 130, 0.7f) {
    resists(HEAT, NATURE, ENERGY, PHYSICAL)
    weakTo(COLD)
}

val corruptedEnemy = enemy("Corrupted Enemy", CORRUPTED, UnitTexture.ENEMY, 95, 0.9f) {
    resists(MYSTIC)
    weakTo(NATURE, ENERGY)
}

val tundraEnemy = enemy("Tundra Enemy", TUNDRA, UnitTexture.ENEMY, 110, 1f) {
    resists(COLD, NATURE)
    weakTo(ENERGY, HEAT, PHYSICAL)
}

val cloudEnemy = enemy("Cloud Enemy", CLOUD, UnitTexture.ENEMY, 75, 1.25f) {
    resists(COLD, PHYSICAL, ENERGY)
    weakTo(MYSTIC, HEAT)
}

val techEnemy = enemy("Tech Enemy", TECH, UnitTexture.ENEMY, 105, 0.95f) {
    resists(MYSTIC, PHYSICAL, NATURE, COLD)
    weakTo(ENERGY)
}

val mountainEnemy = enemy("Mountain Enemy", MOUNTAIN, UnitTexture.ENEMY, 115, 0.8f) {
    resists(PHYSICAL, HEAT, ENERGY)
    weakTo(COLD, NATURE)
}

val enchantedEnemy = enemy("Enchanted Enemy", ENCHANTED, UnitTexture.ENEMY, 60, 1.2f) {
    resists(ENERGY, COLD, NATURE)
    weakTo(MYSTIC)
}

val possibleEnemies = mapOf(
    GRASSLAND to listOf(grasslandEnemy),
    FOREST to listOf(forestEnemy),
    DESERT to listOf(desertEnemy),
    LAVA to listOf(lavaEnemy),
    CORRUPTED to listOf(corruptedEnemy),
    TUNDRA to listOf(tundraEnemy),
    CLOUD to listOf(cloudEnemy),
    TECH to listOf(techEnemy),
    MOUNTAIN to listOf(mountainEnemy),
    ENCHANTED to listOf(enchantedEnemy)
)
