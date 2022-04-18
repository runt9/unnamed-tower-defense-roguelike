package com.runt9.untdrl.view.duringRun.ui.shop

import com.badlogic.gdx.Graphics
import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.model.event.RunStateUpdated
import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.service.duringRun.LootService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.DialogController
import com.runt9.untdrl.view.duringRun.SHOP_REROLL_COST
import ktx.async.onRenderingThread

class ShopDialogController(
    graphics: Graphics,
    private val runStateService: RunStateService,
    private val eventBus: EventBus,
    private val lootService: LootService
) : DialogController() {
    override val vm = ShopDialogViewModel()
    override val view = ShopDialogView(this, vm, graphics.width, graphics.height)

    override fun load() {
        eventBus.registerHandlers(this)
        runStateService.load().applyNewState()
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
    }

    @HandlesEvent
    suspend fun runStateUpdated(event: RunStateUpdated) = onRenderingThread { event.newState.applyNewState() }

    private fun RunState.applyNewState() {
        vm.rerollCost(shopRerollCost)
        vm.gold(gold)
        vm.shop(currentShop)
    }

    fun done() {
        hide()
    }

    fun reroll() {
        val newShop = lootService.generateShop()
        runStateService.update {
            currentShop = newShop
            gold -= vm.rerollCost.get()
            shopRerollCost += SHOP_REROLL_COST
        }
    }

    fun <T : LootItem> buyItem(item: T, cost: Int): Boolean {
        if (cost > vm.gold.get()) return false

        runStateService.update {
            when (item) {
                is Relic -> {
                    relics += item
                    currentShop.relics -= item
                }

                is Consumable -> {
                    consumables += item
                    currentShop.consumables -= item
                }

                is TowerCore -> {
                    cores += item
                    currentShop.cores -= item
                }
            }

            gold -= cost
        }

        return true
    }
}
