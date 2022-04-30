package com.runt9.untdrl.view.mainMenu

import com.runt9.untdrl.util.framework.ui.view.ScreenView
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel
import ktx.actors.onClick
import ktx.scene2d.textButton
import ktx.scene2d.vis.visLabel

class MainMenuView(
    override val controller: MainMenuScreenController,
    override val vm: ViewModel
) : ScreenView() {
    override fun init() {
        super.init()

        visLabel("Untitled Tower Defense Roguelike", "title").cell(row = true, spaceBottom = 20f)

        textButton("New Run", "round") {
            onClick { this@MainMenuView.controller.newRun() }
        }.cell(row = true, spaceBottom = 10f)

        textButton("Settings", "round") {
            onClick { this@MainMenuView.controller.showSettings() }
        }.cell(row = true, spaceBottom = 10f)

        textButton("Quit", "round") {
            onClick { this@MainMenuView.controller.exit() }
        }.cell(row = true)
    }
}
