package com.runt9.untdrl.view.duringRun.ui.shop

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.model.loot.LootItem
import com.runt9.untdrl.util.ext.ui.bindButtonDisabled
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.framework.ui.view.DialogView
import com.runt9.untdrl.view.duringRun.ui.util.lootItem
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.KTable
import ktx.scene2d.textButton
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

class ShopDialogView(
    override val controller: ShopDialogController,
    override val vm: ShopDialogViewModel,
    screenWidth: Int,
    screenHeight: Int
) : DialogView(controller, vm, "Shop", screenWidth, screenHeight) {
    override val widthScale: Float = 0.75f
    override val heightScale: Float = 0.75f

    private fun <T : LootItem> KTable.renderShopRow(label: String, items: Map<T, Int>) {
        visTable {
            visLabel(label).cell(row = true, colspan = 5, pad = 5f, align = Align.left, expandX = true)

            items.forEach { (item, cost) ->
                lootItem(item) {
                    row()

                    visLabel("G $cost") {
                        bindUpdatable(vm.gold) {
                            color = if (vm.gold.get() >= cost) Color.WHITE else Color.RED
                        }
                    }

                    onClick { if (controller.buyItem(item, cost)) remove() }
                }.cell(grow = true)
            }
        }.cell(row = true, grow = true, pad = 10f)
    }

    override fun KTable.initContentTable() {
        val vm = vm

        visTable {
            bindUpdatable(vm.shop) {
                clear()
                val shop = vm.shop.get()
                renderShopRow("Relics", shop.relics)
                renderShopRow("Consumables", shop.consumables)
                renderShopRow("Tower Cores", shop.cores)
            }
        }.cell(grow = true)
    }

    override fun KTable.initButtons() {
        visTable {
            visLabel("") { bindLabelText { "Gold: ${vm.gold()}" } }.cell(grow = true, padRight = 20f)

            textButton("Done") {
                onChange {
                    controller.done()
                }
            }.cell(grow = true)

            textButton("") {
                bindUpdatable(vm.rerollCost) {
                    setText("Reroll (G ${vm.rerollCost.get()})")
                }

                bindButtonDisabled(listOf(vm.rerollCost, vm.gold)) {
                    vm.rerollCost.get() > vm.gold.get()
                }

                onChange {
                    controller.reroll()
                }
            }.cell(grow = true, padLeft = 20f)
        }.cell(grow = true)
    }
}
