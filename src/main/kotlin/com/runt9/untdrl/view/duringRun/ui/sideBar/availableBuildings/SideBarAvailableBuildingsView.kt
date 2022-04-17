package com.runt9.untdrl.view.duringRun.ui.sideBar.availableBuildings

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onClick
import ktx.scene2d.stack
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visTable

class SideBarAvailableBuildingsView(override val controller: SideBarAvailableBuildingsController, override val vm: SideBarAvailableBuildingsViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        visScrollPane {
            setScrollingDisabled(true, false)
            setFlickScroll(false)

            flowGroup(spacing = 2f) {
                bindUpdatable(vm.availableBuildings) {
                    vm.availableBuildings.get().forEach { building ->
                        stack {
                            squarePixmap(60, Color.LIGHT_GRAY)
                            visImage(controller.loadTexture(building.texture))
                            visTable {
                                visLabel(building.goldCost.toString()) {
                                    bindUpdatable(vm.gold) {
                                        color = if (vm.gold.get() >= building.goldCost) Color.WHITE else Color.RED
                                    }
                                }.cell(expand = true, align = Align.bottomRight)
                            }

                            onClick {
                                controller.addBuilding(building)
                            }
                        }
                    }
                }
            }
        }.cell(grow = true, row = true)

    }
}
