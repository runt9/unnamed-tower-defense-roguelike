package com.runt9.untdrl.view.duringRun.ui.sideBar.availableBuildings

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.model.attribute.definition.displayName
import com.runt9.untdrl.model.attribute.definition.getDisplayValue
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.separator
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onClick
import ktx.scene2d.KStack
import ktx.scene2d.stack
import ktx.scene2d.tooltip
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
                            buildingTooltip(building)
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

fun KStack.buildingTooltip(building: BuildingDefinition) = tooltip {
    background(VisUI.getSkin().getDrawable("panel1"))

    visLabel(building.name).cell(growX = true, row = true, pad = 4f)

    separator(2f)

    visLabel(building.description) {
        wrap = true
    }.cell(growX = true, row = true, pad = 5f)

    separator(2f)

    visTable {
        building.attrs.forEach { (type, value) ->
            visLabel(type.displayName).cell(pad = 2f, align = Align.left, growX = true)
            visLabel(type.getDisplayValue(value.baseValue)).cell(row = true, pad = 2f, align = Align.right)
        }
    }.cell(grow = true)
}
