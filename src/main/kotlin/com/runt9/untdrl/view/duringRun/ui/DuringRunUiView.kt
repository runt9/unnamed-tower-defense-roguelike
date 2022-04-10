package com.runt9.untdrl.view.duringRun.ui

import com.badlogic.gdx.graphics.Color
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.ScreenView
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
            vm.placingChunk.bind {
                if (!vm.placingChunk.get()) {
                    textButton("Add Chunk") {
                        onChange {
                            controller.addChunk()
                            remove()
                        }
                    }.cell(row = true)
                }
            }
            vm.placingTower.bind {
                if (!vm.placingTower.get()) {
                    textButton("Add Tower") {
                        onChange {
                            controller.addTower()
                            remove()
                        }
                    }.cell(row = true)
                }
            }

            textButton("Spawn Enemies") {
                onChange {
                    controller.spawnEnemies()
                }
            }.cell(row = true)
        }.cell(grow = true, row = true)
    }
}
