package com.runt9.untdrl.util.ext.ui

import com.badlogic.gdx.graphics.Color
import ktx.scene2d.KTable
import ktx.scene2d.vis.visTable

fun KTable.separator(height: Float, color: Color = Color.BLACK) = visTable {
    background(rectPixmapTexture(1, 1, color).toDrawable())
}.cell(row = true, height = height, growX = true, padTop = height / 2f, padBottom = height / 2f)

