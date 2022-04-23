package com.runt9.untdrl.view.duringRun.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.model.enemy.Biome
import com.runt9.untdrl.model.enemy.Chunk
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
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.service.duringRun.IndexedGridGraph
import com.runt9.untdrl.service.duringRun.ProjectileService
import com.runt9.untdrl.service.duringRun.RunStateService
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
    private val projectileService: ProjectileService,
    private val grid: IndexedGridGraph,
    private val randomizer: RandomizerService,
    private val runStateService: RunStateService
) : Controller {
    private val logger = unTdRlLogger()
    override val vm = DuringRunGameViewModel()
    override val view = DuringRunGameView(this, vm)
    private val children = mutableListOf<Controller>()
    private var nextChunk: Chunk? = null

    override fun load() {
        eventBus.registerHandlers(this)
        addHomeChunk()
        placeGoldAndResearchBuildings()
        eventBus.enqueueEventSync(PrepareNextWaveEvent())
    }

    private fun addHomeChunk() {
        val chunk = Chunk(chunkGenerator.buildHomeChunk(), randomizer.randomize { Biome.values().random(randomizer.rng) })
        chunk.position.set(HOME_POINT)
        val chunkVm = ChunkViewModel(chunk)
        chunkVm.isPlaced(true)
        chunkVm.isValidPlacement(true)
        vm.chunks += chunkVm
        grid.addChunk(chunk)
    }

    private fun placeGoldAndResearchBuildings() {
        val addBuilding: suspend (BuildingDefinition, Vector2) -> Unit = { def: BuildingDefinition, point: Vector2 ->
            val building = Building(def, assets[def.texture.assetFile])
            buildingService.recalculateAttrs(building)
            building.action = buildingService.injectBuildingAction(building)
            building.position.set(point)

            val buildingVm = BuildingViewModel(building)
            buildingVm.isValidPlacement(true)
            vm.buildings += buildingVm
            buildingService.add(building)
        }

        val emptyTiles = grid.emptyTiles().map { it.point }.shuffled(randomizer.rng).take(2)
        runStateService.load().apply {
            // TODO: Future: Allow player to select one of multiple instead of hard coded
            launchOnRenderingThread {
                addBuilding(faction.goldBuildings[0], emptyTiles[0])
                addBuilding(faction.researchBuildings[0], emptyTiles[1])
            }
        }
    }

    @HandlesEvent(NewChunkEvent::class)
    suspend fun handleNewChunk() = onRenderingThread {
        if (nextChunk == null) {
            nextChunk = Chunk(chunkGenerator.generateGrid(), randomizer.randomize { Biome.values().random(randomizer.rng) })
        }

        vm.chunks += ChunkViewModel(nextChunk!!)
    }

    @HandlesEvent
    suspend fun handleNewBuilding(event: NewBuildingEvent) = onRenderingThread {
        val buildingDef = event.buildingDefinition
        val building = buildingService.newBuilding(buildingDef)

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

        projectile.onDie { onRenderingThread {
            vm.projectiles -= projVm
        }}

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
