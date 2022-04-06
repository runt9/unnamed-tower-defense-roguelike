package com.runt9.untdrl.view.duringRun.game.enemy

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.framework.ui.view.GroupView
import ktx.scene2d.vis.visImage

class EnemyView(override val controller: EnemyController, override val vm: EnemyViewModel) : GroupView(controller, vm) {
    override fun init() {
        val vm = vm

        setSize(0.75f, 0.75f)
        setBounds(0f, 0f, 0.75f, 0.75f)
        setOrigin(Align.center)
        bindUpdatable(vm.position) { vm.position.get().apply { setPosition(x, y) } }

        val image = visImage(vm.texture) {
            setSize(0.75f, 0.75f)
            setOrigin(Align.center)
            bindUpdatable(vm.rotation) { vm.rotation.get().apply { rotation = this } }
        }
    }
}
