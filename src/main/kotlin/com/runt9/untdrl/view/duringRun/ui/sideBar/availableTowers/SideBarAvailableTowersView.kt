package com.runt9.untdrl.view.duringRun.ui.sideBar.availableTowers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.model.attribute.definition.displayName
import com.runt9.untdrl.model.attribute.definition.getDisplayValue
import com.runt9.untdrl.model.tower.definition.TowerDefinition
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

class SideBarAvailableTowersView(override val controller: SideBarAvailableTowersController, override val vm: SideBarAvailableTowersViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        visScrollPane {
            setScrollingDisabled(true, false)
            setFlickScroll(false)

            flowGroup(spacing = 2f) {
                bindUpdatable(vm.availableTowers) {
                    vm.availableTowers.get().forEach { tower ->
                        stack {
                            squarePixmap(60, Color.LIGHT_GRAY)
                            visImage(controller.loadTexture(tower.texture))
                            towerTooltip(tower)
                            visTable {
                                visLabel(tower.goldCost.toString()) {
                                    bindUpdatable(vm.gold) {
                                        color = if (vm.gold.get() >= tower.goldCost) Color.WHITE else Color.RED
                                    }
                                }.cell(expand = true, align = Align.bottomRight)
                            }

                            onClick {
                                controller.addTower(tower)
                            }
                        }
                    }
                }
            }
        }.cell(grow = true, row = true)
    }
}

fun KStack.towerTooltip(tower: TowerDefinition) = tooltip {
    background(VisUI.getSkin().getDrawable("panel1"))

    visLabel(tower.name).cell(growX = true, row = true, pad = 4f)

    separator(2f)

    visLabel(tower.description) {
        wrap = true
    }.cell(growX = true, row = true, pad = 5f)

    separator(2f)

    visTable {
        tower.attrs.forEach { (type, value) ->
            visLabel(type.displayName).cell(pad = 2f, align = Align.left, growX = true)
            visLabel(type.getDisplayValue(value.baseValue)).cell(row = true, pad = 2f, align = Align.right)
        }
    }.cell(grow = true)
}