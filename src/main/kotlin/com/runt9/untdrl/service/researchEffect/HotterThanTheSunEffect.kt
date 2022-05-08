package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.faction.HotterThanTheSunDefinition
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.flamethrower
import com.runt9.untdrl.model.tower.intercept.beforeDamage
import com.runt9.untdrl.model.tower.intercept.onAttack
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.max

class HotterThanTheSunEffect(
    override val eventBus: EventBus,
    private val definition: HotterThanTheSunDefinition,
    private val towerService: TowerService,
    private val tickerRegistry: TickerRegistry
) : ResearchEffect {
    private val trackers = mutableMapOf<Tower, Tracker>()

    override fun apply() {
        towerService.forEachTower(::applyToTower)

        tickerRegistry.registerTicker { delta ->
            trackers.values.forEach { it.tick(delta) }
        }
    }

    private fun applyToTower(tower: Tower) {
        if (tower.definition != flamethrower) return
        val tracker = Tracker()
        trackers[tower] = tracker

        tower.addInterceptor(onAttack { _, _ ->
            tracker.attacked()
        })

        tower.addInterceptor(beforeDamage { _, dr ->
            val stacks = tracker.stacks
            if (stacks == 0) return@beforeDamage

            dr.addDamageMultiplier(stacks * definition.damagePerStack)
        })
    }

    @HandlesEvent
    fun towerPlaced(event: TowerPlacedEvent) {
        applyToTower(event.tower)
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        trackers.values.forEach(Tracker::reset)
    }

    private inner class Tracker {
        private val decayTimer = Timer(1f)
        private val stackTimer = Timer(1f)
        private var inCombat = false
        var stacks = 0
            private set

        fun tick(delta: Float) {
            decayTimer.tick(delta)

            if (decayTimer.isReady) {
                inCombat = false
                stacks = max(0, stacks - 1)
                decayTimer.reset()
                stackTimer.reset(false)
            }

            if (!inCombat) return

            stackTimer.tick(delta)

            if (stackTimer.isReady) {
                stacks++
                stackTimer.reset()
            }
        }

        fun attacked() {
            inCombat = true
            decayTimer.reset(false)
        }

        fun reset() {
            stacks = 0
            decayTimer.reset(false)
            stackTimer.reset(false)
        }
    }
}
