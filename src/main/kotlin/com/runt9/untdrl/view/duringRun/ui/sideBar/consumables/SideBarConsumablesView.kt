package com.runt9.untdrl.view.duringRun.ui.sideBar.consumables

import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onClick
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visTable

class SideBarConsumablesView(override val controller: SideBarConsumablesController, override val vm: SideBarConsumablesViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        visScrollPane {
            setScrollingDisabled(true, false)
            setFlickScroll(false)

            flowGroup(spacing = 5f) {
                bindUpdatable(vm.consumables) {
                    clear()
                    vm.consumables.get().forEach { consumable ->
                        visTable {
                            squarePixmap(55, consumable.color)

                            onClick {
                                controller.useConsumable(consumable)
                            }
                        }
                    }
                }
            }
        }.cell(grow = true, row = true)
    }
}
