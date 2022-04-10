package com.runt9.untdrl.view.duringRun.ui

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.NewTowerEvent
import com.runt9.untdrl.model.event.SpawnEnemiesEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller

class DuringRunUiController(private val eventBus: EventBus) : Controller {
    override val vm = DuringRunUiViewModel()
    override val view = DuringRunUiView(this, vm)
    private val children = mutableListOf<Controller>()

    override fun load() {
        eventBus.registerHandlers(this)
    }

    override fun dispose() {
        children.forEach(Disposable::dispose)
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun addChunk() {
        vm.placingChunk(true)
        eventBus.enqueueEventSync(NewChunkEvent())
    }

    @HandlesEvent(ChunkPlacedEvent::class)
    fun chunkPlaced() {
        vm.placingChunk(false)
    }

    fun addTower() {
        vm.placingTower(true)
        eventBus.enqueueEventSync(NewTowerEvent())
    }

    @HandlesEvent(TowerPlacedEvent::class)
    fun towerPlaced() {
        vm.placingTower(false)
    }

    fun spawnEnemies() {
        eventBus.enqueueEventSync(SpawnEnemiesEvent())
    }
}
