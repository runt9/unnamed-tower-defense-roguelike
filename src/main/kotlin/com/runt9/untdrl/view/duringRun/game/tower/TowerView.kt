package com.runt9.untdrl.view.duringRun.game.tower

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.GroupView
import ktx.actors.alpha
import ktx.scene2d.vis.visImage

class TowerView(override val controller: TowerController, override val vm: TowerViewModel) : GroupView(controller, vm) {
    private val logger = unTdRlLogger()
    override fun init() {
        val vm = vm

        setSize(0.75f, 0.75f)
        setBounds(0f, 0f, 0.75f, 0.75f)
        setOrigin(Align.center)
        bindUpdatable(vm.position) { vm.position.get().apply { setPosition(x, y, Align.center) } }

        bindUpdatable(vm.isValidPlacement) {
            alpha = if (vm.isValidPlacement.get()) 1f else 0.5f
        }

        visImage(vm.texture) {
            setSize(0.75f, 0.75f)
            setOrigin(Align.center)
            bindUpdatable(vm.rotation) { vm.rotation.get().apply { rotation = this } }
        }
    }
}
