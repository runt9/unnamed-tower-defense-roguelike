package com.runt9.untdrl.view.duringRun.ui.bottomBar

import com.badlogic.gdx.graphics.Color
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.squarePixmap
import com.runt9.untdrl.util.framework.ui.view.TableView
import ktx.actors.onClick
import ktx.scene2d.stack
import ktx.scene2d.vis.visImage

class BottomBarView(override val controller: BottomBarController, override val vm: BottomBarViewModel) : TableView(controller, vm) {
    override fun init() {
        val controller = controller

        bindUpdatable(vm.availableTowers) {
            vm.availableTowers.get().forEach { tower ->
                stack {
                    squarePixmap(60, Color.LIGHT_GRAY)
                    visImage(controller.loadTexture(tower.texture))

                    onClick {
                        controller.addTower(tower)
                    }
                }.cell(space = 2f)
            }
        }
    }
}
