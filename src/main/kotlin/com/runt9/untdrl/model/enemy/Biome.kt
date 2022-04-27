package com.runt9.untdrl.model.enemy

import com.badlogic.gdx.graphics.Color

enum class Biome(val spawnerColor: Color) {
    GRASSLAND(Color.GREEN),
    FOREST(Color.FOREST),
    DESERT(Color.TAN),
    LAVA(Color.FIREBRICK),
    CORRUPTED(Color.BLACK),
    TUNDRA(Color.valueOf("CCFFFF")),
    CLOUD(Color.valueOf("33AAFF")),
    TECH(Color.SLATE),
    MOUNTAIN(Color.LIGHT_GRAY),
    ENCHANTED(Color.PINK)
}
