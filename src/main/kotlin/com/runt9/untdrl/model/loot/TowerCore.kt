package com.runt9.untdrl.model.loot

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.util.ext.displayName
import kotlinx.serialization.Serializable

@Serializable
class TowerCore(override val rarity: Rarity, val modifiers: List<AttributeModifier>) : LootItem {
    override val name = "${rarity.displayName()} Tower Core"
    override val type = LootItemType.CORE
    override val description by lazy { generateDescription() }

    private fun generateDescription(): String {
        val sb = StringBuilder()
        modifiers.forEach { sb.append("${it.name}\n") }
        return sb.toString()
    }
}
