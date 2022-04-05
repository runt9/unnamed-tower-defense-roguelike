package com.runt9.untdrl.util.framework.ui.core

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import ktx.app.KtxScreen

interface UnTdRlScreen : KtxScreen {
    val stages: List<UnTdRlStage>

    override fun render(delta: Float) = stages.forEach { it.render(delta) }
    override fun hide() = stages.forEach(Stage::clear)

    override fun dispose() {
        stages.forEach(Disposable::dispose)
    }
}

