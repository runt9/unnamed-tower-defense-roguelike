package com.runt9.untdrl.model.enemy

import com.badlogic.gdx.graphics.Color

enum class Biome(val spawnerColor: Color) {
    DESERT(Color.TAN),
    VOLCANO(Color.FIREBRICK),
    GRASSLAND(Color.FOREST),
    TUNDRA(Color.valueOf("CCFFFF")),
    CLOUD(Color.valueOf("33AAFF"))
}
