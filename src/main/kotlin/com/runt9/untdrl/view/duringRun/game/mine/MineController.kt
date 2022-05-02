package com.runt9.untdrl.view.duringRun.game.mine

import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.controller.lazyInjectView
import com.runt9.untdrl.util.framework.ui.uiComponent
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl

@Scene2dDsl
fun <S> KWidget<S>.mine(mine: MineViewModel, init: MineView.(S) -> Unit = {}) = uiComponent<S, MineController, MineView>({
    this.vm = mine
}, init)

class MineController : Controller {
    override lateinit var vm: MineViewModel
    override val view by lazyInjectView<MineView>()
}
