package com.runt9.untdrl.view.duringRun.game

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.enemy.Biome
import com.runt9.untdrl.model.enemy.Chunk
import com.runt9.untdrl.model.event.TowerCancelledEvent
import com.runt9.untdrl.model.event.ChunkCancelledEvent
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.NewTowerEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.PrepareNextWaveEvent
import com.runt9.untdrl.model.event.ProjectileSpawnedEvent
import com.runt9.untdrl.service.ChunkGenerator
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.duringRun.IndexedGridGraph
import com.runt9.untdrl.service.duringRun.ProjectileService
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.view.duringRun.HOME_POINT
import com.runt9.untdrl.view.duringRun.game.tower.TowerViewModel
import com.runt9.untdrl.view.duringRun.game.chunk.ChunkViewModel
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileViewModel
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread

// TODO: Can probably break this into multiple controllers: enemies, towers, projectiles, all floating groups
class DuringRunGameController(
    private val eventBus: EventBus,
    private val assets: AssetStorage,
    private val chunkGenerator: ChunkGenerator,
    private val towerService: TowerService,
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

    @HandlesEvent(NewChunkEvent::class)
    suspend fun handleNewChunk() = onRenderingThread {
        if (nextChunk == null) {
            nextChunk = Chunk(chunkGenerator.generateGrid(), randomizer.randomize { Biome.values().random(randomizer.rng) })
        }

        vm.chunks += ChunkViewModel(nextChunk!!)
    }

    @HandlesEvent
    suspend fun handleNewTower(event: NewTowerEvent) = onRenderingThread {
        val towerDef = event.towerDefinition
        val tower = towerService.newTower(towerDef)

        val towerVm = TowerViewModel(tower)
        tower.onMove {
            towerVm.rotation(rotation)
        }
        vm.towers += towerVm
    }

    @HandlesEvent
    suspend fun towerCancelled(event: TowerCancelledEvent) = onRenderingThread {
        vm.towers.removeIf { it.tower == event.tower }
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
