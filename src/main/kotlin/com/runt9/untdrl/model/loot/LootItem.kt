package com.runt9.untdrl.model.loot

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable

sealed interface LootItem {
    val color: Color
}
