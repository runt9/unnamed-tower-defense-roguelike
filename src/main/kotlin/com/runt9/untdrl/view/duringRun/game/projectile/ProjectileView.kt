package com.runt9.untdrl.view.duringRun.game.projectile

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.loadTexture
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.framework.ui.view.GroupView
import ktx.assets.async.AssetStorage
import ktx.scene2d.vis.visImage

class ProjectileView(
    override val controller: ProjectileController,
    override val vm: ProjectileViewModel,
    val assets: AssetStorage
) : GroupView() {
    override fun init() {
        val vm = vm

        setSize(0.25f, 0.25f)
        setBounds(0f, 0f, 0.25f, 0.25f)
        setOrigin(Align.center)
        bindUpdatable(vm.position) { vm.position.get().apply { setPosition(x, y, Align.center) } }

        visImage(assets.loadTexture(vm.texture.get())) {
            setSize(0.25f, 0.25f)
            setOrigin(Align.center)
            bindUpdatable(vm.rotation) { vm.rotation.get().apply { rotation = this } }
        }
    }
}
