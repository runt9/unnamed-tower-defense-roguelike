package com.runt9.untdrl.view.duringRun.game

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.event.BuildingCancelledEvent
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.NewBuildingEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.service.ChunkGenerator
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.service.duringRun.ProjectileService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.view.duringRun.HOME_POINT
import com.runt9.untdrl.view.duringRun.game.building.BuildingViewModel
import com.runt9.untdrl.view.duringRun.game.chunk.ChunkViewModel
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileViewModel
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread

// TODO: Can probably break this into multiple controllers: enemies, buildings, projectiles, all floating groups
class DuringRunGameController(
    private val eventBus: EventBus,
    private val assets: AssetStorage,
    private val chunkGenerator: ChunkGenerator,
    private val buildingService: BuildingService,
    private val projectileService: ProjectileService
) : Controller {
    private val logger = unTdRlLogger()
    override val vm = DuringRunGameViewModel()
    override val view = DuringRunGameView(this, vm)
    private val children = mutableListOf<Controller>()
    private var nextChunk: Chunk? = null

    override fun load() {
        eventBus.registerHandlers(this)
        addHomeChunk()
        eventBus.enqueueEventSync(PrepareNextWaveEvent())
    }

    private fun addHomeChunk() {
        val chunk = Chunk(chunkGenerator.buildHomeChunk())
        chunk.position.set(HOME_POINT)
        val chunkVm = ChunkViewModel(chunk)
        chunkVm.isPlaced(true)
        chunkVm.isValidPlacement(true)
        vm.chunks += chunkVm
        eventBus.enqueueEventSync(ChunkPlacedEvent(chunk))
    }

    @HandlesEvent(NewChunkEvent::class)
    suspend fun handleNewChunk() = onRenderingThread {
        if (nextChunk == null) {
            nextChunk = Chunk(chunkGenerator.generateGrid())
        }

        vm.chunks += ChunkViewModel(nextChunk!!)
    }

    @HandlesEvent
    suspend fun handleNewBuilding(event: NewBuildingEvent) = onRenderingThread {
        val buildingDef = event.buildingDefinition
        val building = Building(buildingDef, assets[buildingDef.texture.assetFile])
        buildingService.recalculateAttrs(building)
        building.action = buildingService.injectBuildingAction(building)

        val buildingVm = BuildingViewModel(building)
        building.onMove {
            buildingVm.rotation(rotation)
        }
        vm.buildings += buildingVm
    }

    @HandlesEvent
    suspend fun buildingCancelled(event: BuildingCancelledEvent) = onRenderingThread {
        vm.buildings.removeIf { it.building == event.building }
    }

    @HandlesEvent
    suspend fun handleEnemySpawn(event: EnemySpawnedEvent) = onRenderingThread {
        val enemy = event.enemy
        val enemyVm = EnemyViewModel(enemy)

        enemy.onMove {
            enemyVm.position(position.cpy())
            enemyVm.rotation(rotation)
        }

        vm.enemies += enemyVm
    }

    @HandlesEvent
    suspend fun enemyRemoved(event: EnemyRemovedEvent) = onRenderingThread {
        vm.enemies.removeIf { it.enemy == event.enemy }
    }

    @HandlesEvent
    suspend fun spawnProjectile(event: ProjectileSpawnedEvent) = onRenderingThread {
        val projectile = event.projectile
        val projVm = ProjectileViewModel(projectile)

        projectile.onMove {
            projVm.position(position.cpy())
            projVm.rotation(rotation)
        }

        projectile.onDie {
            vm.projectiles -= projVm
        }

        vm.projectiles += projVm
        projectileService.add(projectile)
    }

    @HandlesEvent
    suspend fun chunkCancelled(event: ChunkCancelledEvent) = onRenderingThread {
        vm.chunks.removeIf { it.chunk == event.chunk }
        event.chunk.rotation = 0f
    }

    @HandlesEvent
    suspend fun chunkPlaced(event: ChunkPlacedEvent) = onRenderingThread {
        nextChunk = null
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        clearChildren()
        super.dispose()
    }

    private fun clearChildren() {
        children.forEach(Disposable::dispose)
        children.clear()
    }

    fun addChild(controller: Controller) = children.add(controller)
}
