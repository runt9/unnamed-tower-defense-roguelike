package com.runt9.untdrl.view.duringRun.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.ScreenView
import com.runt9.untdrl.view.duringRun.ui.sideBar.sideBar
import com.runt9.untdrl.view.duringRun.ui.util.lootItem
import ktx.scene2d.vis.flowGroup
import ktx.scene2d.vis.visTable

class DuringRunUiView(override val controller: DuringRunUiController, override val vm: DuringRunUiViewModel) : ScreenView(controller, vm) {
    override fun init() {
        super.init()

        val controller = controller
        val vm = vm

        visTable {
            flowGroup (spacing = 5f) {
                bindUpdatable(vm.relics) {
                    clear()

                    vm.relics.get().forEach { relic ->
                        lootItem(relic)
                    }
                }
            }.cell(expand = true, align = Align.topLeft, pad = 5f)
        }.cell(grow = true)

        sideBar {
            controller.addChild(this.controller)
            background(rectPixmapTexture(1, 1, Color.SLATE).toDrawable())
        }.cell(growY = true, width = 220f)
    }
}
