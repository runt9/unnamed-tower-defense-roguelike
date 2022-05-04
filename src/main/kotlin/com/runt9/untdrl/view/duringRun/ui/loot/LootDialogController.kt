package com.runt9.untdrl.view.duringRun.ui.loot

import com.runt9.untdrl.model.loot.Consumable
import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.model.loot.Relic
import com.runt9.untdrl.model.loot.TowerCore
import com.runt9.untdrl.service.duringRun.LootService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.framework.ui.controller.DialogController
import com.runt9.untdrl.util.framework.ui.controller.injectView
import kotlin.math.min

class LootDialogController(private val lootService: LootService, private val runStateService: RunStateService) : DialogController() {
    override val vm = LootDialogViewModel()
    override val view = injectView<LootDialogView>()

    override fun load() {
        // TODO: Determine if doing a copy is needed
        lootService.lootPool.apply {
            vm.lootedGold(gold)
            vm.lootedItems(items)
        }
        runStateService.load().apply {
            vm.maxGoldInPurse(goldPurseMax)
            vm.maxItemSelections(lootItemMax)
        }
    }

    fun fillGoldPurse() {
        if (vm.goldSelected.get() != 0) return

        val gold = min(vm.lootedGold.get(), vm.maxGoldInPurse.get())
        vm.lootedGold(vm.lootedGold.get() - gold)
        vm.goldSelected(gold)
    }

    fun done() {
        lootService.takeLoot(vm.goldSelected.get(), vm.selectedItems.get())
        hide()
    }

    fun lootItem(item: LootItem) {
        if (vm.selectedItems.get().size >= vm.maxItemSelections.get()) return
        val runState = runStateService.load()
        if (item is Consumable && runState.consumables.size >= runState.consumableSlots) return

        vm.lootedItems -= item
        vm.selectedItems += item
    }

    fun deselectItem(item: LootItem) {
        vm.selectedItems -= item
        vm.lootedItems += item
    }
}
