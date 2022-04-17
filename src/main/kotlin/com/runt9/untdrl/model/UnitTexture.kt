package com.runt9.untdrl.model

enum class UnitTexture(textureFile: String) {
    PROJECTILE("heroArrow-tp.png"),
    PROTOTYPE_TOWER("bossArrow-tp.png"),
    GOLD_MINE("goldArrow-tp.png"),
    RESEARCH_LAB("blueArrow-tp.png"),
    ENEMY("redArrow-tp.png");

    val assetFile = "unit/$textureFile"
}
