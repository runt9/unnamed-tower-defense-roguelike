package com.runt9.untdrl.view.duringRun.game.enemy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.framework.ui.view.GroupView
import ktx.scene2d.progressBar
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visTable
import ktx.style.progressBar

class EnemyView(override val controller: EnemyController, override val vm: EnemyViewModel) : GroupView(controller, vm) {
    override fun init() {
        val vm = vm

        setSize(0.5f, 0.5f)
        setBounds(0f, 0f, 0.5f, 0.5f)
        setOrigin(Align.center)
        bindUpdatable(vm.position) { vm.position.get().apply { setPosition(x, y, Align.center) } }

        visTable {
            setRound(false)
            setSize(0.5f, 0.2f)
            y = 0.5f

            progressBar {
                style = this@EnemyView.unitBarStyle(Color.GREEN)

                bindUpdatable(vm.hpPercent) { value = vm.hpPercent.get() }

                setAnimateDuration(0.15f)
                setSize(0.75f, 0.2f)
                setOrigin(Align.center)
                setRound(false)
            }.cell(height = 0.2f, width = 0.5f, row = true)
        }

        visImage(vm.texture) {
            setSize(0.5f, 0.5f)
            setOrigin(Align.center)
            bindUpdatable(vm.rotation) { vm.rotation.get().apply { rotation = this } }
        }
    }

    private fun unitBarStyle(color: Color) = VisUI.getSkin().progressBar {
        background = rectPixmapTexture(1, 1, Color.DARK_GRAY).toDrawable()
        background.minHeight = 0.05f
        background.minWidth = 0f
        knobBefore = rectPixmapTexture(1, 1, color).toDrawable()
        knobBefore.minHeight = 0.05f
        knobBefore.minWidth = 0f
    }
}
