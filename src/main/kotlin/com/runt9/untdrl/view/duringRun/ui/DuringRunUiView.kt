package com.runt9.untdrl.view.duringRun.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.ScreenView
import com.runt9.untdrl.view.duringRun.ui.sideBar.sideBar
import ktx.scene2d.vis.visTable

class DuringRunUiView(override val controller: DuringRunUiController, override val vm: DuringRunUiViewModel) : ScreenView(controller, vm) {
    override fun init() {
        super.init()

        val controller = controller
        val vm = vm

        visTable().cell(grow = true)
        sideBar {
            controller.addChild(this.controller)
            background(rectPixmapTexture(1, 1, Color.SLATE).toDrawable())
        }.cell(growY = true, width = Gdx.graphics.width * 0.15f)
    }
}
