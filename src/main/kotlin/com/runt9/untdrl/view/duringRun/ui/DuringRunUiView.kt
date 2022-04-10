package com.runt9.untdrl.view.duringRun.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindVisible
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.ScreenView
import com.runt9.untdrl.view.duringRun.ui.bottomBar.bottomBar
import com.runt9.untdrl.view.duringRun.ui.topBar.topBar
import ktx.actors.onChange
import ktx.scene2d.textButton
import ktx.scene2d.vis.visTable

class DuringRunUiView(override val controller: DuringRunUiController, override val vm: DuringRunUiViewModel) : ScreenView(controller, vm) {
    override fun init() {
        super.init()

        val controller = controller
        val vm = vm

        topBar {
            controller.addChild(this.controller)
            background(rectPixmapTexture(1, 40, Color.SLATE).toDrawable())
        }.cell(growX = true, height = 40f, row = true)

        visTable {
            bindVisible(vm.actionsVisible, true)

            textButton("Add Chunk") {
                bindVisible(vm.chunkPlacementRequired, true)

                onChange {
                    controller.addChunk()
                }
            }.cell(row = true)

            textButton("Start Wave") {
                bindVisible(vm.chunkPlacementRequired, false)

                onChange {
                    controller.startWave()
                }
            }.cell(row = true)
        }.cell(expand = true, row = true, align = Align.bottomRight)

        bottomBar {
            controller.addChild(this.controller)
            background(rectPixmapTexture(1, 60, Color.SLATE).toDrawable())
        }.cell(growX = true, height = 60f)
    }
}
