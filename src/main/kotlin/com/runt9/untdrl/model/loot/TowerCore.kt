package com.runt9.untdrl.model.loot

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.loot.definition.LegendaryPassiveDefinition
import com.runt9.untdrl.util.ext.displayName
import kotlinx.serialization.Serializable

@Serializable
class TowerCore(override val rarity: Rarity, val modifiers: List<AttributeModifier>, val passive: LegendaryPassiveDefinition?) : LootItem {
    override val name = "${rarity.displayName()} Tower Core"
    override val type = LootItemType.CORE
    override val description by lazy { generateDescription() }

    private fun generateDescription(): String {
        val lines = mutableListOf<String>()
        modifiers.forEach { lines += it.name }
        if (passive != null) {
            lines += (passive.description)
        }
        return lines.joinToString("\n")
    }
}
