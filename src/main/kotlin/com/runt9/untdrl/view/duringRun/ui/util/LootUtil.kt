package com.runt9.untdrl.view.duringRun.ui.util

import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.util.ext.ui.separator
import com.runt9.untdrl.util.ext.ui.squarePixmap
import ktx.scene2d.KWidget
import ktx.scene2d.tooltip
import ktx.scene2d.vis.KVisTable
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

fun <S> KWidget<S>.lootItem(item: LootItem, init: KVisTable.() -> Unit = {}) = visTable {
    squarePixmap(60, item.type.color)
    tooltip {
        it.setInstant(true)
        it.manager.animations = false
        background(VisUI.getSkin().getDrawable("panel1"))

        visLabel(item.name).cell(growX = true, row = true, pad = 4f)

        separator(2f)

        visLabel(item.description) { wrap = true }.cell(growX = true, row = true, pad = 5f)
    }
    init()
}
