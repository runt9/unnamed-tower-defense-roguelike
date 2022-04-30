package com.runt9.untdrl.view.duringRun.ui.research

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.VisUI
import com.runt9.untdrl.model.research.ResearchDefinition
import com.runt9.untdrl.util.ext.loadTexture
import com.runt9.untdrl.util.ext.ui.bindButtonDisabled
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.ext.ui.separator
import com.runt9.untdrl.util.framework.ui.view.DialogView
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.KTable
import ktx.scene2d.textButton
import ktx.scene2d.tooltip
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

class ResearchDialogView(
    override val controller: ResearchDialogController,
    override val vm: ResearchDialogViewModel,
    private val assets: AssetStorage
) : DialogView(controller, "Research") {
    override val widthScale: Float = 0.75f
    override val heightScale: Float = 0.45f

    override fun KTable.initContentTable() {
        val vm = vm

        visTable {
            bindUpdatable(vm.research) {
                clear()
                val research = vm.research.get()

                if (research.isEmpty()) {
                    visLabel("No more research is available").cell(grow = true, pad = 10f)
                } else {
                    research.forEach { r ->
                        visTable {
                            visImage(assets.loadTexture(r.icon)).cell(row = true)
                            researchTooltip(r)
                            visLabel("R ${r.cost}") {
                                bindUpdatable(vm.researchAmount) {
                                    color = if (vm.researchAmount.get() >= r.cost) Color.WHITE else Color.RED
                                }
                            }

                            onClick { controller.applyResearch(r) }
                        }.cell(grow = true)
                    }
                }
            }
        }.cell(grow = true)
    }

    override fun KTable.initButtons() {
        visTable {
            visLabel("") { bindLabelText { "Gold: ${vm.gold()}" } }.cell(grow = true, padRight = 20f)
            visLabel("") { bindLabelText { "Research: ${vm.researchAmount()}" } }.cell(grow = true, padRight = 20f)

            textButton("Done") {
                onChange {
                    controller.done()
                }
            }.cell(grow = true)

            textButton("") {
                bindUpdatable(vm.rerollCost) {
                    setText("Reroll (G ${vm.rerollCost.get()})")
                }

                bindButtonDisabled(listOf(vm.rerollCost, vm.gold)) {
                    vm.rerollCost.get() > vm.gold.get()
                }

                onChange {
                    controller.reroll()
                }
            }.cell(grow = true, padLeft = 20f)
        }.cell(grow = true)
    }
}

fun Actor.researchTooltip(research: ResearchDefinition) = tooltip {
    it.setInstant(true)
    background(VisUI.getSkin().getDrawable("panel1"))

    visLabel(research.name).cell(growX = true, row = true, pad = 4f)

    separator(2f)

    visLabel(research.description) {
        wrap = true
    }.cell(growX = true, row = true, pad = 5f, minWidth = 50f)
}
