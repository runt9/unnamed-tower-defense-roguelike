package com.runt9.untdrl.view.duringRun.ui.faction

import com.runt9.untdrl.util.ext.displayInt
import com.runt9.untdrl.util.ext.ui.bindLabelText
import com.runt9.untdrl.util.framework.ui.view.DialogView
import ktx.actors.onChange
import ktx.collections.toGdxArray
import ktx.scene2d.KTable
import ktx.scene2d.textButton
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visSelectBoxOf
import ktx.scene2d.vis.visSlider
import ktx.scene2d.vis.visTable

class ManageFactionDialogView(
    override val controller: ManageFactionDialogController,
    override val vm: ManageFactionDialogViewModel
) : DialogView(controller, "Manage Faction") {
    override val widthScale = 0.5f
    override val heightScale = 0.5f

    override fun KTable.initContentTable() {
        visLabel("Stock Market:", "title-plain").cell(row = true, colspan = 2)

        visLabel("Investment Percentage:").cell()

        visTable {
            visSlider(vm.minInvestPct.get() * 100, vm.maxInvestPct.get() * 100, step = 1f) {
                value = vm.investmentPct.get() * 100
                onChange {
                    vm.investmentPct(value / 100)
                }
            }
            visLabel("") {
                bindLabelText { "${(vm.investmentPct() * 100).displayInt()}%" }
            }
        }.cell(row = true)

        visLabel("Risk Tolerance:").cell()
        val selectBox = visSelectBoxOf(vm.riskToleranceOptions.get().toGdxArray())
        selectBox.selected = vm.riskTolerance.get()
        selectBox.onChange {
            vm.riskTolerance(selectBox.selected)
        }
        selectBox.cell(row = true)

        visLabel("R&D Budget:", "title-plain").cell(row = true, colspan = 2)
        visLabel("Profit to Research Percentage:").cell()
        visTable {
            visSlider(vm.minProfitPct.get() * 100, vm.maxProfitPct.get() * 100, step = 1f) {
                value = vm.profitPct.get() * 100
                onChange {
                    vm.profitPct(value / 100)
                }
            }
            visLabel("") {
                bindLabelText { "${(vm.profitPct() * 100).displayInt()}%" }
            }
        }.cell(row = true)
    }

    override fun KTable.initButtons() {
        textButton("Cancel") { onChange { controller.cancel() } }
        textButton("Done") { onChange { controller.done() } }
    }
}
