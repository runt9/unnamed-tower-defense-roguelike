package com.runt9.untdrl.view.duringRun.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.model.Projectile
import com.runt9.untdrl.model.Tower
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.event.ChunkPlacedEvent
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.model.event.NewTowerEvent
import com.runt9.untdrl.model.event.SpawnEnemiesEvent
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.path.IndexedGridGraph
import com.runt9.untdrl.service.ChunkGenerator
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.view.duringRun.game.chunk.ChunkViewModel
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel
import com.runt9.untdrl.view.duringRun.game.projectile.ProjectileViewModel
import com.runt9.untdrl.view.duringRun.game.tower.TowerViewModel
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread
import kotlin.math.roundToInt

// TODO: Can probably break this into multiple controllers: enemies, towers, projectiles, all floating groups
class DuringRunGameController(
    private val eventBus: EventBus,
    private val assets: AssetStorage,
    private val enemyService: EnemyService,
    private val towerService: TowerService,
    private val chunkGenerator: ChunkGenerator,
    private val grid: IndexedGridGraph
) : Controller {
    private val logger = unTdRlLogger()
    override val vm = DuringRunGameViewModel()
    override val view = DuringRunGameView(this, vm, chunkGenerator)
    private val children = mutableListOf<Controller>()

    override fun load() {
        eventBus.registerHandlers(this)
        addHomeChunk()
    }

    private fun addHomeChunk() {
        val chunk = Chunk(chunkGenerator.buildHomeChunk(), Vector2(7f, 4f))
        val chunkVm = ChunkViewModel(chunk)
        chunkVm.isPlaced(true)
        chunkVm.isValidPlacement(true)
        grid.addChunk(chunk)
        vm.chunks += chunkVm
    }

    @HandlesEvent(NewChunkEvent::class)
    suspend fun handleNewChunk() = onRenderingThread {
        val chunk = chunkGenerator.generateGrid()
        vm.chunks += ChunkViewModel(Chunk(chunk))
    }

    @HandlesEvent(NewTowerEvent::class)
    suspend fun handleNewTower() = onRenderingThread {
        val tower = Tower(texture = assets[UnitTexture.BOSS.assetFile], projTexture = assets[UnitTexture.HERO.assetFile])

        val towerVm = TowerViewModel(tower)
        tower.onRotate { towerVm.rotation(rotation) }
        tower.onProj { spawnProjectile(this) }
        vm.towers += towerVm
    }

    @HandlesEvent
    suspend fun towerPlaced(event: TowerPlacedEvent) = onRenderingThread {
        towerService.add(event.tower)
    }

    private fun spawnProjectile(projectile: Projectile) {
        val projVm = ProjectileViewModel(projectile)

        projectile.onMove {
            projVm.position(position.cpy())
            projVm.rotation(rotation)
        }

        projectile.onDie {
            vm.projectiles -= projVm
        }

        vm.projectiles += projVm
    }

    @HandlesEvent
    suspend fun chunkPlaced(event: ChunkPlacedEvent) = onRenderingThread {
        val chunk = event.chunk
        grid.addChunk(chunk)
        grid.recalculateSpawnerPaths()
    }

    @HandlesEvent(SpawnEnemiesEvent::class)
    suspend fun spawnEnemies() = onRenderingThread {
        grid.spawners.forEach {
            val enemy = it.spawnEnemy(assets[UnitTexture.ENEMY.assetFile])
            enemyService.add(enemy)
            val enemyVm = EnemyViewModel(enemy)

            enemy.onMove {
                if (position.dst(grid.home.point).roundToInt() == 0) {
                    vm.enemies -= enemyVm
                    enemyService.remove(enemy)
                }

                enemyVm.position(position.cpy())
                enemyVm.rotation(rotation)
            }

            vm.enemies += enemyVm
        }
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
