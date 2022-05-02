package com.runt9.untdrl.view.duringRun.ui.sideBar.tower

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.model.attribute.definition.displayName
import com.runt9.untdrl.model.attribute.definition.getDisplayValue
import com.runt9.untdrl.model.tower.TargetingMode
import com.runt9.untdrl.model.tower.specialization.TowerSpecializationDefinition
import com.runt9.untdrl.util.ext.loadTexture
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.separator
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.view.duringRun.TOWER_SPECIALIZATION_LEVEL
import com.runt9.untdrl.view.duringRun.ui.util.lootItem
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.collections.toGdxArray
import ktx.scene2d.label
import ktx.scene2d.progressBar
import ktx.scene2d.stack
import ktx.scene2d.textButton
import ktx.scene2d.tooltip
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visSelectBoxOf
import ktx.scene2d.vis.visTable
import ktx.style.progressBar

class SideBarTowerView(
    override val controller: SideBarTowerController,
    override val vm: SideBarTowerViewModel,
    val assets: AssetStorage
) : TableView() {
    override fun init() {
        val controller = controller
        val vm = vm
        val assets = assets

        visTable {
            visLabel(vm.name.get()).cell(row = true, pad = 2f)

            separator(2f)

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

            separator(2f)

            visTable {
                bindUpdatable(vm.attrs) {
                    clear()
                    vm.attrs.get().forEach { (type, value) ->
                        visLabel(type.displayName) { wrap = true }.cell(pad = 2f, align = Align.left, growX = true)
                        visLabel(type.getDisplayValue(value)).cell(row = true, pad = 2f, align = Align.right)
                    }
                }
            }.cell(row = true, growX = true)

            separator(2f)

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

                                            lootItem(core) {
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
                                            lootItem(core)
                                        }
                                    }
                                }
                            }
                        }.cell(grow = true, row = true, pad = 4f)
                    }
                }
            }.cell(grow = true, row = true)

            separator(2f)

            visTable {
                bindUpdatable(vm.canSpecialize) {
                    clear()
                    if (vm.canSpecialize.get()) {
                        bindUpdatable(vm.hasSelectedSpecialization) {
                            clear()
                            if (vm.hasSelectedSpecialization.get()) {
                                visLabel("Selected Specialization: ${vm.selectedSpecializationName.get()}") {
                                    wrap = true
                                    setAlignment(Align.center)
                                }.cell(grow = true, pad = 10f)
                            } else {
                                vm.specializations.get().forEach { specialization ->
                                    visTable {
                                        visImage(assets.loadTexture(specialization.icon)) {
                                            setSize(50f, 50f)
                                            onClick { controller.applySpecialization(specialization) }
                                            specializationTooltip(specialization)
                                        }.cell(grow = true)


                                    }.cell(expand = true, pad = 10f, height = 50f, width = 50f)
                                }
                            }
                        }
                    } else {
                        visLabel("Specializations available at level $TOWER_SPECIALIZATION_LEVEL") {
                            wrap = true
                            setAlignment(Align.center)
                        }.cell(grow = true, pad = 10f)
                    }
                }
            }.cell(grow = true, row = true, pad = 4f)

            separator(2f)

            visTable {
                bindUpdatable(vm.canChangeTargetingMode) {
                    clear()

                    visLabel("Targeting Mode:").cell(growX = true, row = true, padBottom = 2f)

                    val selectBox = visSelectBoxOf(TargetingMode.values().toGdxArray())
                    selectBox.selected = vm.targetingMode.get()
                    selectBox.onChange {
                        vm.targetingMode(selectBox.selected)
                        controller.targetingModeChange(selectBox.selected)
                    }
                }
            }.cell(growX = true, row = true, pad = 4f)
        }.cell(row = true, grow = true, align = Align.top, pad = 4f)
    }
}

fun Actor.specializationTooltip(specialization: TowerSpecializationDefinition) = tooltip {
    it.setInstant(true)
    background(VisUI.getSkin().getDrawable("panel1"))

    visLabel(specialization.name).cell(growX = true, row = true, pad = 4f)

    separator(2f)

    visLabel(specialization.description) {
        wrap = true
    }.cell(growX = true, row = true, pad = 5f, minWidth = 250f)
}
