package com.runt9.untdrl.view.loading

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.percent
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.ScreenView
import ktx.scene2d.vis.visLabel
import kotlin.math.roundToInt

class LoadingScreenView(
    override val controller: LoadingScreenController,
    override val vm: LoadingScreenViewModel
) : ScreenView() {
    private val logger = unTdRlLogger()

    override fun init() {
        logger.debug { "Initializing" }
        super.init()
        visLabel("", "title-plain") {
            bindLabelText { "Loading ${this@LoadingScreenView.vm.loadingPercent().percent().roundToInt()}%" }
        }.cell(expand = true, align = Align.center)
    }
}
