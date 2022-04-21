package com.runt9.untdrl.view.duringRun.ui.util

import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.util.ext.ui.squarePixmap
import ktx.scene2d.KWidget
import ktx.scene2d.textTooltip
import ktx.scene2d.vis.KVisTable
import ktx.scene2d.vis.visTable

fun <S> KWidget<S>.lootItem(item: LootItem, init: KVisTable.() -> Unit = {}) = visTable {
    squarePixmap(60, item.type.color)
    textTooltip(item.description)
    init()
}
