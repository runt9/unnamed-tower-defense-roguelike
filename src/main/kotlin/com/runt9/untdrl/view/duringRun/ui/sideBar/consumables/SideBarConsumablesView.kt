package com.runt9.untdrl.view.duringRun.ui.sideBar.consumables

import com.badlogic.gdx.graphics.Color
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.view.duringRun.ui.util.lootItem
import ktx.actors.onClick
import ktx.scene2d.stack
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

class SideBarConsumablesView(
    override val controller: SideBarConsumablesController,
    override val vm: SideBarConsumablesViewModel
) : TableView() {
    override fun init() {
        val vm = vm
        val controller = controller

        visTable {
            visLabel("Consumables:").cell(growX = true, pad = 5f, row = true)

            flowGroup(spacing = 5f) {
                bindUpdatable(vm.consumables) {
                    clear()

                    repeat(vm.maxConsumables.get()) { i ->
                        stack {
                            squarePixmap(60, Color.LIGHT_GRAY)

                            vm.consumables.get().getOrNull(i)?.also { consumable ->
                                lootItem(consumable) {
                                    onClick {
                                        // TODO: Handle right-click -> discard
                                        controller.useConsumable(consumable)
                                    }
                                }
                            }
                        }
                    }
                }
            }.cell(row = true)
        }.cell(growX = true, row = true, pad = 5f)
    }
}
