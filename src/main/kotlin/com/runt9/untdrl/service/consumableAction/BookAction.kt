package com.runt9.untdrl.service.consumableAction

import com.runt9.untdrl.model.loot.definition.BookDefinition
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.unTdRlLogger

class BookAction(
    private val definition: BookDefinition,
    private val towerService: TowerService
) : ConsumableAction {
    private val logger = unTdRlLogger()

    override fun canApply() = true

    override fun apply() {
        towerService.forEachTower { t ->
            towerService.gainXpSync(t, definition.xpAmt)
        }
    }
}
