package com.runt9.untdrl.service.towerAction

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.amountPerInterval
import com.runt9.untdrl.model.tower.gainInterval
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus
import kotlin.math.roundToInt

class GenerateGoldAction(
    private val tower: Tower,
    override val eventBus: EventBus,
    private val towerService: TowerService,
    private val runStateService: RunStateService
) : TowerAction {
    private val generateTimer = Timer(tower.gainInterval)

    override suspend fun act(delta: Float) {
        if (tower.gainInterval != generateTimer.targetTime) {
            generateTimer.targetTime = tower.gainInterval
        }

        generateTimer.tick(delta)

        if (generateTimer.isReady) {
            runStateService.update {
                gold += tower.amountPerInterval.roundToInt()
            }

            towerService.gainXp(tower, 1)
            generateTimer.reset()
        }
    }
}
