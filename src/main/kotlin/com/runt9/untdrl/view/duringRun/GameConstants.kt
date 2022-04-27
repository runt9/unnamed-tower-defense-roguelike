package com.runt9.untdrl.view.duringRun

import com.badlogic.gdx.math.Vector2

const val GAME_AREA_WIDTH = 16f
const val GAME_AREA_HEIGHT = 9f
const val CHUNK_SIZE = 9
const val GAME_WIDTH_MARGIN = ((GAME_AREA_WIDTH / 16f) * 2)
const val GAME_HEIGHT_MARGIN = ((GAME_AREA_HEIGHT / 9f) * 2)
const val GAME_WIDTH = GAME_AREA_WIDTH - GAME_WIDTH_MARGIN
const val GAME_HEIGHT = GAME_AREA_HEIGHT - GAME_HEIGHT_MARGIN

val HOME_POINT = Vector2(7f, 4f)
const val REROLL_COST = 50
const val MAX_TOWER_LEVEL = 20
const val SHOP_ITEMS = 3
