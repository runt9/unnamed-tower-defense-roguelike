package com.runt9.untdrl.view.duringRun.ui.sideBar.building

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.model.attribute.definition.displayName
import com.runt9.untdrl.model.attribute.definition.getDisplayValue
import com.runt9.untdrl.model.building.BuildingType
import com.runt9.untdrl.model.building.TargetingMode
import com.runt9.untdrl.model.loot.LootItemType
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.collections.toGdxArray
import ktx.scene2d.label
import ktx.scene2d.progressBar
import ktx.scene2d.stack
import ktx.scene2d.textButton
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visSelectBoxOf
import ktx.scene2d.vis.visTable
import ktx.style.progressBar

class SideBarBuildingView(override val controller: SideBarBuildingController, override val vm: SideBarBuildingViewModel) : TableView(controller, vm) {
    override fun init() {
        val controller = controller
        val vm = vm

        visTable {
            val separator = {
                visTable {
                    background(rectPixmapTexture(1, 1, Color.BLACK).toDrawable())
                }.cell(row = true, height = 2f, growX = true, padTop = 2f, padBottom = 2f)
            }

            visLabel(vm.name.get()).cell(row = true, pad = 2f)

            separator()

            visTable {
                visLabel("") { bindLabelText { "Level: ${vm.level()}" } }.cell(pad = 2f, align = Align.left)
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

                        bindUpdatable(vm.xp) { value = vm.xp.get().toFloat() / vm.xpToLevel.get() }

                        setSize(100f, 20f)
                        setOrigin(Align.center)
                        setRound(false)
                    }

                    label("") {
                        bindLabelText { "${vm.xp()} / ${vm.xpToLevel()}" }
                        setAlignment(Align.center)
                    }
                }.cell(width = 100f, height = 20f, row = true)
            }.cell(row = true)

            separator()

            visTable {
                bindUpdatable(vm.attrs) {
                    clear()
                    vm.attrs.get().forEach { (type, value) ->
                        visLabel(type.displayName) { wrap = true }.cell(pad = 2f, align = Align.left, growX = true)
                        visLabel(type.getDisplayValue(value)).cell(row = true, pad = 2f, align = Align.right)
                    }
                }
            }.cell(row = true, growX = true)

            separator()

            visTable {
                bindUpdatable(vm.coreInventoryShown) {
                    clear()
                    if (vm.coreInventoryShown.get()) {
                        visLabel("Core Inventory:").cell(growX = true, row = true, padBottom = 4f)

                        visScrollPane {
                            setScrollingDisabled(true, false)

                            flowGroup(spacing = 5f) {
                                bindUpdatable(vm.coreInventory) {
                                    clear()
                                    vm.coreInventory.get().forEach { core ->
                                        stack {
                                            squarePixmap(60, Color.DARK_GRAY)

                                            visTable {
                                                squarePixmap(55, LootItemType.CORE.color)
                                                onClick {
                                                    controller.placeCore(core)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }.cell(grow = true, row = true)

                        textButton("Close") {
                            onChange { controller.closeCoreInventory() }
                        }
                    } else {
                        visLabel("Tower Cores:").cell(growX = true, row = true, padBottom = 2f)

                        flowGroup(spacing = 5f) {
                            bindUpdatable(vm.cores) {
                                clear()
                                repeat(vm.maxCores.get()) { i ->
                                    stack {
                                        squarePixmap(60, Color.LIGHT_GRAY)

                                        val core = vm.cores.get().getOrNull(i)

                                        if (core == null) {
                                            onClick {
                                                controller.openCoreInventory()
                                            }
                                        } else {
                                            squarePixmap(55, LootItemType.CORE.color)
                                        }
                                    }
                                }
                            }
                        }.cell(grow = true, row = true, pad = 4f)
                    }
                }
            }.cell(grow = true, row = true)

            separator()

            visTable {
                visLabel("") { bindLabelText { "Upgrade Points: ${vm.upgradePoints()}" } }.cell(growX = true, row = true, padBottom = 2f)

                visTable {
                    bindUpdatable(vm.availableUpgrades) {
                        clear()
                        val upgrades = vm.availableUpgrades.get()

                        if (upgrades.isEmpty()) {
                            visLabel("No upgrades available for this tower") {
                                wrap = true
                                setAlignment(Align.center)
                            }.cell(grow = true, pad = 10f)
                        } else {
                            upgrades.forEach { upgrade ->
                                visImage(controller.loadTexture(upgrade.icon)) {
                                    setSize(50f, 50f)
                                    onClick { controller.applyUpgrade(upgrade) }
                                }.cell(expand = true, pad = 10f, height = 50f, width = 50f)
                            }
                        }
                    }
                }.cell(grow = true, row = true, pad = 4f)
            }.cell(grow = true, row = true)

            if (vm.type.get() == BuildingType.TOWER) {
                separator()

                visTable {
                    visLabel("Targeting Mode:").cell(growX = true, row = true, padBottom = 2f)

                    val selectBox = visSelectBoxOf(TargetingMode.values().toGdxArray())
                    selectBox.selected = vm.targetingMode.get()
                    selectBox.onChange {
                        vm.targetingMode(selectBox.selected)
                        controller.targetingModeChange(selectBox.selected)
                    }
                }.cell(growX = true, row = true, pad = 4f)
            }
        }.cell(row = true, grow = true, align = Align.top, pad = 4f)
    }
}
