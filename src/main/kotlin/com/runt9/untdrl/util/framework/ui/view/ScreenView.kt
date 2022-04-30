package com.runt9.untdrl.util.framework.ui.view

abstract class ScreenView : TableView() {
    override fun init() {
        setSize(stage.width, stage.height)
    }
}
