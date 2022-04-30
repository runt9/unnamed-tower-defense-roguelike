package com.runt9.untdrl.view.settings

import com.badlogic.gdx.utils.Align
import com.runt9.untdrl.util.ext.ui.bindButtonDisabledToVmDirty
import com.runt9.untdrl.util.ext.ui.bindChecked
import com.runt9.untdrl.util.framework.ui.view.DialogView
import ktx.actors.onChange
import ktx.scene2d.KTable
import ktx.scene2d.checkBox
import ktx.scene2d.textButton

class SettingsDialogView(
    override val controller: SettingsDialogController,
    override val vm: SettingsDialogViewModel
) : DialogView(controller, "Settings") {
    override val widthScale = 0.33f
    override val heightScale = 0.5f

    override fun KTable.initContentTable() {
        val vm = this@SettingsDialogView.vm

        checkBox("Fullscreen", "switch") { bindChecked(vm.fullscreen) }.cell(row = true, align = Align.left)
        checkBox("VSync", "switch") { bindChecked(vm.vsync) }.cell(row = true, align = Align.left)
    }

    override fun KTable.initButtons() {
        val vm = this@SettingsDialogView.vm
        val controller = this@SettingsDialogView.controller

        textButton("Apply") {
            bindButtonDisabledToVmDirty(vm, false)
            onChange { controller.applySettings() }
        }

        // TODO: Confirmation dialog if unsaved changes
        textButton("Done") {
            onChange { controller.hide() }
        }
    }
}

