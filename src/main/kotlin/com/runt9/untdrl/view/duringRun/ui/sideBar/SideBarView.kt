package com.runt9.untdrl.view.duringRun.ui.sideBar

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.bindVisible
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.progressBar
import ktx.scene2d.stack
import ktx.scene2d.textButton
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visTable
import ktx.style.progressBar

// TODO: Break the pieces into their own modules
class SideBarView(override val controller: SideBarController, override val vm: SideBarViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        infoPanel()
        separator()

        visTable {
            bindUpdatable(vm.selectedBuilding) {
                clear()

                if (vm.selectedBuilding.get().empty) {
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
                    }.cell(grow = true)
                } else {
                    val building = vm.selectedBuilding.get()

                    visTable {
                        visLabel(building.name.get()).cell(row = true, pad = 2f, align = Align.left)

                        // TODO: Split stats into 2 cells each so wrapping can work its magic
                        building.stats.get().forEach { (name, value) ->
                            visLabel(name) { wrap = true }.cell(pad = 2f, align = Align.left, growX = true)
                            visLabel(value).cell(row = true, pad = 2f, align = Align.right, growX = true)
                        }

                        visLabel("") { bindLabelText { "Level: ${building.level()}" } }.cell(row = true, pad = 2f, align = Align.left)
                        stack {
                            progressBar {
                                style = VisUI.getSkin().progressBar {
                                    background = rectPixmapTexture(2, 2, Color.DARK_GRAY).toDrawable()
                                    background.minHeight = 20f
                                    background.minWidth = 0f
                                    knobBefore = rectPixmapTexture(2, 2, Color.BLUE).toDrawable()
                                    knobBefore.minHeight = 20f
                                    knobBefore.minWidth = 0f
                                }

                                bindUpdatable(building.xp) { value = building.xp.get().toFloat() / building.xpToLevel.get() }

                                setSize(100f, 20f)
                                setOrigin(Align.center)
                                setRound(false)
                            }

                            label("") {
                                bindLabelText { "${building.xp()} / ${building.xpToLevel()}" }
                                setAlignment(Align.center)
                            }
                        }.cell(width = 100f, height = 20f, row = true)

                    }.cell(row = true, growX = true, expandY = true, align = Align.top, pad = 4f)
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

    private fun infoPanel() {
        val vm = vm
        val controller = controller

        visTable {
            textButton("Menu") {
                onChange { controller.menuButtonClicked() }
            }.cell(row = true, expandX = true, align = Align.right)
            visLabel("") { bindLabelText { "Wave: ${vm.wave()}" } }.cell(row = true, pad = 2f, align = Align.left)
            visLabel("") { bindLabelText { "HP: ${vm.hp()}" } }.cell(row = true, pad = 2f, align = Align.left)
            visLabel("") { bindLabelText { "Gold: ${vm.gold()}" } }.cell(row = true, pad = 2f, align = Align.left)
            visLabel("") { bindLabelText { "Research: ${vm.research()}" } }.cell(row = true, pad = 2f, align = Align.left)
        }.cell(row = true, growX = true)
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
