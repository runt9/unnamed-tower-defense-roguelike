package com.runt9.untdrl.model.loot

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class Consumable : LootItem {
    @Contextual
    override val color: Color = Color.BLUE
}
