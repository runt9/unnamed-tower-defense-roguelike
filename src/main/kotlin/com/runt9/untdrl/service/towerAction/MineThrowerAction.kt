package com.runt9.untdrl.service.towerAction

import com.runt9.untdrl.model.event.MineSpawnedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Mine
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.attackTime
import com.runt9.untdrl.model.tower.definition.MineThrowerActionDefinition
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.duringRun.IndexedGridGraph
import com.runt9.untdrl.service.towerAction.subAction.AttackSubAction
import com.runt9.untdrl.util.ext.positionToLocation
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class MineThrowerAction(
    private val definition: MineThrowerActionDefinition,
    private val tower: Tower,
    override val eventBus: EventBus,
    private val grid: IndexedGridGraph,
    private val randomizer: RandomizerService
) : TowerAction {
    private val logger = unTdRlLogger()

    private val attack = AttackSubAction(tower, tower.attackTime, { true }, this::throwMine)

    override suspend fun act(delta: Float) {
        // Easy way to avoid another callback, just check this every tick, it's not expensive
        if (tower.attackTime != attack.timer.targetTime) {
            attack.timer.targetTime = tower.attackTime
        }

        attack.timer.tick(delta)
        attack.act(delta)
    }

    private suspend fun throwMine() {
        val pathPoint = grid.pathTiles().map { it.point }.filter(tower::inRangeOf).random(randomizer.rng)
        pathPoint.add(randomizer.range(-0.25f..0.25f), randomizer.range(-0.25f..0.25f))
        val mine = Mine(tower, definition.mineTexture, positionToLocation(pathPoint))
        eventBus.enqueueEvent(MineSpawnedEvent(mine))
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveEnd() {
        attack.timer.reset(false)
    }
}
