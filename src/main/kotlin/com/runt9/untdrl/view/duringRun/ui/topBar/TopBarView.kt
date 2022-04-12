package com.runt9.untdrl.view.duringRun.ui.topBar

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onChange
import ktx.scene2d.textButton
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

class TopBarView(override val controller: TopBarController, override val vm: TopBarViewModel) : TableView(controller, vm) {
    override fun init() {
        val vm = vm
        val controller = controller

        visTable {
            visLabel("") { bindLabelText { "HP: ${vm.hp()}" } }.cell(expand = true)
            visLabel("") { bindLabelText { "Gold: ${vm.gold()}" } }.cell(expand = true)
        }.cell(expand = true, align = Align.left, padLeft = 5f)

        visTable {
            visLabel("") { bindLabelText { "Wave ${vm.wave()}" } }.cell(expand = true)

            textButton("Menu") {
                onChange { controller.menuButtonClicked() }
            }
        }.cell(expand = true, align = Align.right, padRight = 5f)
    }
}
