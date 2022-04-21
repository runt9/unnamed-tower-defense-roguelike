package com.runt9.untdrl.view.factionSelect

import com.runt9.untdrl.util.framework.ui.view.ScreenView
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.button
import ktx.scene2d.buttonGroup
import ktx.scene2d.textButton
import ktx.scene2d.textField
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

class FactionSelectView(override val controller: FactionSelectController, override val vm: FactionSelectViewModel) : ScreenView(controller, vm) {
    override fun init() {
        super.init()
        val vm = vm
        val controller = controller

        visLabel("Select Faction", "title-plain").cell(row = true, spaceBottom = 5f)

        vm.factionOptions.forEach { faction ->
            visTable {
                buttonGroup(1, 1) {
                    button(style = "toggle") {
                        visLabel(faction.name).cell(row = true)

                        onClick {
                            vm.selectedFaction = faction
                        }
                    }
                }
            }.cell(space = 5f)
        }

        row()

        // TODO: Probably some max length validation or something
        textField {
            messageText = "Enter Seed?"

            onChange { vm.seed(text) }
        }.cell(row = true, spaceBottom = 5f)

        textButton("Start", "round") { onChange { controller.startRun() } }.cell(row = true, spaceBottom = 5f)
        textButton("Back", "round") { onChange { controller.back() } }

    }
}
