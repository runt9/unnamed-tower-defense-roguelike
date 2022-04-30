package com.runt9.untdrl.view.factionSelect

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.framework.ui.view.ScreenView
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.button
import ktx.scene2d.buttonGroup
import ktx.scene2d.textButton
import ktx.scene2d.textField
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visTable

class FactionSelectView(
    override val controller: FactionSelectController,
    override val vm: FactionSelectViewModel,
    val assets: AssetStorage
) : ScreenView() {
    override fun init() {
        super.init()
        val vm = vm
        val controller = controller

        visLabel("Select Faction", "title-plain").cell(row = true, spaceBottom = 5f)

        visTable {
            visTable {
                visScrollPane {
                    setScrollingDisabled(true, false)
                    setFlickScroll(false)

                    visTable {
                        vm.factionOptions.get().forEach { faction ->
                            visTable {
                                buttonGroup(1, 1) {
                                    button(style = "toggle") {
                                        visLabel(faction.name).cell(row = true)

                                        onClick {
                                            vm.selectedFaction(faction)
                                        }
                                    }.cell(growX = true, height = 50f)
                                }.cell(growX = true)
                            }.cell(growX = true, pad = 5f, row = true, align = Align.top)
                        }
                    }
                }.cell(grow = true)
            }.cell(grow = true, pad = 8f)

            visTable {
                bindUpdatable(vm.selectedFaction) {
                    clear()

                    vm.selectedFaction.get().also { faction ->
                        visLabel(faction.name, "title-plain").cell(growX = true, pad = 10f, row = true)

                        visTable {
                            visLabel("Gold Income Passive:").cell(growX = true, pad = 5f, row = true)
                            visLabel(faction.goldPassive.description) { wrap = true }.cell(grow = true, padLeft = 10f, row = true)
                        }.cell(growX = true, pad = 10f, row = true)

                        visTable {
                            visLabel("Research Passive:").cell(growX = true, pad = 5f, row = true)
                            visLabel(faction.researchPassive.description) { wrap = true }.cell(grow = true, padLeft = 10f, row = true)
                        }.cell(growX = true, pad = 10f, row = true)
                    }
                }
            }.cell(grow = true)
        }.cell(grow = true, row = true)


        visTable {
            // TODO: Probably some max length validation or something
            textField {
                messageText = "Enter Seed?"

                onChange { vm.seed(text) }
            }.cell(row = true, spaceBottom = 5f, colspan = 2)

            textButton("Start", "round") { onChange { controller.startRun() } }.cell(pad = 10f)
            textButton("Back", "round") { onChange { controller.back() } }
        }
    }
}
