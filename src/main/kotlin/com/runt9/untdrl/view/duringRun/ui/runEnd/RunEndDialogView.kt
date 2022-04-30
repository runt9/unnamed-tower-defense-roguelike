package com.runt9.untdrl.view.duringRun.ui.runEnd

import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.framework.ui.view.DialogView
import ktx.actors.onChange
import ktx.scene2d.KTable
import ktx.scene2d.textButton
import ktx.scene2d.vis.visLabel

class RunEndDialogView(
    override val controller: RunEndDialogController,
    override val vm: RunEndDialogViewModel
) : DialogView(controller, "Run Over") {
    override val widthScale: Float = 0.5f
    override val heightScale: Float = 0.5f

    override fun KTable.initContentTable() {
        val vm = this@RunEndDialogView.vm
        val controller = this@RunEndDialogView.controller

        visLabel("") { bindLabelText { "You ${if (vm.runWon()) "Won" else "Lost"}!" } }.cell(row = true, spaceBottom = 10f)
        textButton("Exit to Main Menu", "round") { onChange { controller.mainMenu() } }.cell(row = true, spaceBottom = 5f)
        textButton("Exit Game", "round") { onChange { controller.exit() } }
    }

    override fun KTable.initButtons() = Unit
}
