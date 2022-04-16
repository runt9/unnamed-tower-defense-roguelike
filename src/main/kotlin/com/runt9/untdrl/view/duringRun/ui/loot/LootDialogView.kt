package com.runt9.untdrl.view.duringRun.ui.loot

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.DialogView
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.KTable
import ktx.scene2d.stack
import ktx.scene2d.textButton
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visTable

class LootDialogView(
    override val controller: LootDialogController,
    override val vm: LootDialogViewModel,
    screenWidth: Int,
    screenHeight: Int
) : DialogView(controller, vm, "Loot", screenWidth, screenHeight) {
    override val widthScale: Float = 0.6f
    override val heightScale: Float = 0.6f

    override fun KTable.initContentTable() {
        val vm = this@LootDialogView.vm
        val controller = this@LootDialogView.controller

        visTable {
            // Left panel with loot
            // TODO: Need max width or something
            visTable {
                visTable {
                    visLabel("Gold Looted").cell(growX = true, pad = 4f, align = Align.left)
                    visLabel("") {
                        bindLabelText { "${vm.lootedGold()}" }
                    }.cell(growX = true, pad = 4f, align = Align.right, row = true)
                }.cell(growX = true, row = true, align = Align.top)

                visTable {
                    background(rectPixmapTexture(1, 1, Color.BLACK).toDrawable())
                }.cell(row = true, height = 2f, growX = true)

                visScrollPane {
                    setScrollingDisabled(true, false)

                    flowGroup(spacing = 4f) {
                        bindUpdatable(vm.lootedItems) {
                            clear()
                            vm.lootedItems.get().forEach { item ->
                                visTable {
                                    squarePixmap(60, item.color)

                                    onClick {
                                        controller.lootItem(item)
                                    }
                                }
                            }
                        }
                    }
                }.cell(grow = true, row = true, padTop = 5f)
            }.cell(grow = true, padTop = 10f)

            // Middle panel with action buttons
            visTable {
                textButton("Fill Gold Purse") {
                    onChange {
                        controller.fillGoldPurse()
                    }
                }
            }.cell(grow = true)

            // Right panel with choices
            visTable {
                visLabel("") { bindLabelText { "Gold purse: ${vm.goldSelected()} / ${vm.maxGoldInPurse()}" } }.cell(expandX = true, row = true)

                flowGroup(spacing = 4f) {
                    bindUpdatable(vm.selectedItems) {
                        clear()
                        repeat(vm.maxItemSelections.get()) { i ->
                            stack {
                                squarePixmap(60, Color.LIGHT_GRAY)

                                vm.selectedItems.get().getOrNull(i)?.also { item ->
                                    visTable {
                                        squarePixmap(55, item.color)

                                        onClick {
                                            controller.deselectItem(item)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.cell(expandX = true)
            }.cell(grow = true)
        }.cell(grow = true)
    }

    override fun KTable.initButtons() {
        textButton("Done") {
            onChange {
                controller.done()
            }
        }
    }
}
