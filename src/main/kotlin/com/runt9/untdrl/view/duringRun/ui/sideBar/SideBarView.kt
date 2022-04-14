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
import kotlin.math.roundToInt

// TODO: Break the pieces into their own modules
class SideBarView(override val controller: SideBarController, override val vm: SideBarViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        infoPanel()
        separator()

        visTable {
            bindUpdatable(vm.selectedTower) {
                clear()

                if (vm.selectedTower.get().empty) {
                    visScrollPane {
                        setScrollingDisabled(true, false)
                        setFlickScroll(false)

                        flowGroup(spacing = 2f) {
                            bindUpdatable(vm.availableTowers) {
                                vm.availableTowers.get().forEach { tower ->
                                    stack {
                                        squarePixmap(60, Color.LIGHT_GRAY)
                                        visImage(controller.loadTexture(tower.texture))
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
                    }.cell(grow = true)
                } else {
                    val tower = vm.selectedTower.get()

                    visTable {
                        visLabel(tower.name.get()).cell(row = true, pad = 2f, align = Align.left)
                        visLabel("") { bindLabelText { "Damage: ${tower.damage().roundToInt()}" } }.cell(row = true, pad = 2f, align = Align.left)
                        visLabel("") { bindLabelText { "Range: ${tower.range()}" } }.cell(row = true, pad = 2f, align = Align.left)
                        visLabel("") { bindLabelText { "Attack Speed: ${"%.2f".format(1 / tower.attackSpeed())}" } }.cell(row = true, pad = 2f, align = Align.left)
                        visLabel("") { bindLabelText { "Level: ${tower.level()}" } }.cell(row = true, pad = 2f, align = Align.left)
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

                                bindUpdatable(tower.xp) { value = tower.xp.get().toFloat() / tower.xpToLevel.get() }

                                setSize(100f, 20f)
                                setOrigin(Align.center)
                                setRound(false)
                            }

                            label("") {
                                bindLabelText { "${tower.xp()} / ${tower.xpToLevel()}" }
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
