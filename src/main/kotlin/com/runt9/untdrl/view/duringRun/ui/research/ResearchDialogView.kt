package com.runt9.untdrl.view.duringRun.ui.research

import com.badlogic.gdx.graphics.Color
import com.runt9.untdrl.util.ext.ui.bindButtonDisabled
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.ext.ui.bindUpdatable
import com.runt9.untdrl.util.framework.ui.view.DialogView
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.KTable
import ktx.scene2d.textButton
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable

class ResearchDialogView(
    override val controller: ResearchDialogController,
    override val vm: ResearchDialogViewModel,
    screenWidth: Int,
    screenHeight: Int
) : DialogView(controller, vm, "Research", screenWidth, screenHeight) {
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
                            visImage(controller.loadTexture(r.icon)).cell(row = true)
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
