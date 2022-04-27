package com.runt9.untdrl.view.duringRun.game.tower

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.bindVisible
import com.runt9.untdrl.util.ext.ui.circlePixmapTexture
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.GroupView
import ktx.actors.alpha
import ktx.scene2d.vis.visImage

class TowerView(override val controller: TowerController, override val vm: TowerViewModel) : GroupView(controller, vm) {
    private val logger = unTdRlLogger()

    override fun init() {
        val towerSize = 0.75f
        val vm = vm

        setSize(towerSize, towerSize)
        setBounds(0f, 0f, towerSize, towerSize)
        setOrigin(Align.center)
        bindUpdatable(vm.position) { vm.position.get().apply { setPosition(x, y, Align.center) } }

        bindUpdatable(vm.isValidPlacement) {
            alpha = if (vm.isValidPlacement.get()) 1f else 0.5f
        }

        visImage(vm.texture) {
            setSize(towerSize, towerSize)
            setOrigin(Align.center)
            bindUpdatable(vm.rotation) { vm.rotation.get().apply { rotation = this } }
        }

        visImage(circlePixmapTexture(100, false, Color.WHITE)) {
            alpha = 0.5f
            zIndex = 999999

            bindVisible(vm.isSelected, true)

            bindUpdatable(vm.attrs) {
                val attrs = vm.attrs.get()
                attrs[AttributeType.RANGE]?.also {
                    setSize(it * 2, it * 2)
                    setPosition(towerSize / 2, towerSize / 2, Align.center)

                }
            }
        }
    }
}
