package com.runt9.untdrl.view.duringRun.ui

import com.runt9.untdrl.util.framework.ui.view.ScreenView
import ktx.actors.onChange
import ktx.scene2d.textButton
import ktx.scene2d.vis.visTable

class DuringRunUiView(override val controller: DuringRunUiController, override val vm: DuringRunUiViewModel) : ScreenView(controller, vm) {
    override fun init() {
        super.init()

        val controller = controller
        val vm = vm

        visTable {
            vm.placingChunk.bind {
                clear()
                if (!vm.placingChunk.get()) {
                    textButton("Add Chunk") {
                        onChange {
                            controller.addChunk()
                        }
                    }
                }
            }
        }.cell(grow = true, row = true)
    }
}
