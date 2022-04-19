package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.bindVisible
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.view.duringRun.ui.sideBar.availableBuildings.availableBuildings
import com.runt9.untdrl.view.duringRun.ui.sideBar.building.sideBarBuilding
import com.runt9.untdrl.view.duringRun.ui.sideBar.consumables.consumables
import com.runt9.untdrl.view.duringRun.ui.sideBar.infoPanel.infoPanel
import ktx.actors.onChange
import ktx.scene2d.textButton
import ktx.scene2d.vis.visTable

class SideBarView(override val controller: SideBarController, override val vm: SideBarViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        infoPanel().cell(row = true, growX = true)
        separator()

        visTable {
            bindUpdatable(vm.selectedBuilding) {
                clear()
                controller.removeDynamicSidebarControllers()

                if (vm.selectedBuilding.get().empty) {
                    availableBuildings {
                        controller.addChild(this.controller)
                    }.cell(grow = true, row = true)

                    consumables {
                        controller.addChild(this.controller)
                    }.cell(grow = true, row = true)
                } else {
                    sideBarBuilding(vm.selectedBuilding.get()) {
                        controller.addChild(this.controller)
                    }.cell(row = true, grow = true, align = Align.top)
                }
            }
        }.cell(row = true, grow = true)

        separator()
        actionButton()
    }

    private fun separator() {
        visTable {
            background(rectPixmapTexture(1, 1, Color.BLACK).toDrawable())
        }.cell(row = true, height = 4f, growX = true)
    }

    private fun actionButton() {
        val vm = vm
        val controller = controller

        visTable {
            bindVisible(vm.actionsVisible, true)

            bindUpdatable(vm.chunkPlacementRequired) {
                clear()
                val text: String
                val callback: Function<Unit>

                if (vm.chunkPlacementRequired.get()) {
                    text = "Add Chunk"
                    callback = controller::addChunk
                } else {
                    text = "Start Wave"
                    callback = controller::startWave
                }

                textButton(text) {
                    onChange {
                        callback()
                    }
                }.cell(row = true)
            }
        }.cell(row = true, pad = 4f)
    }
}
