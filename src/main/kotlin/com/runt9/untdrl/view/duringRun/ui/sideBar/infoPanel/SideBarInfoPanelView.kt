package com.runt9.untdrl.view.duringRun.ui.sideBar.infoPanel

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onChange
import ktx.scene2d.textButton
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

class SideBarInfoPanelView(override val controller: SideBarInfoPanelController, override val vm: SideBarInfoPanelViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        visTable {
            visTable {
                textButton("Shop") { onChange { controller.shopButtonClicked() } }.cell(expandX = true)
                textButton("Research") { onChange { controller.researchButtonClicked() } }.cell(expandX = true)
                textButton("Menu") { onChange { controller.menuButtonClicked() } }.cell(expandX = true)
            }.cell(expandX = true, row = true)

            visTable {
                visLabel("") { bindLabelText { "Wave: ${vm.wave()}" } }.cell(row = true, pad = 2f, align = Align.left)
                visLabel("") { bindLabelText { "HP: ${vm.hp()}" } }.cell(row = true, pad = 2f, align = Align.left)
                visLabel("") { bindLabelText { "Gold: ${vm.gold()}" } }.cell(row = true, pad = 2f, align = Align.left)
                visLabel("") { bindLabelText { "Research: ${vm.research()}" } }.cell(row = true, pad = 2f, align = Align.left)
            }.cell(expandX = true, row = true, pad = 5f, align = Align.left)
        }.cell(row = true, growX = true)

    }
}
