package com.runt9.untdrl.view.duringRun.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.model.UnitTexture
import com.runt9.untdrl.model.event.NewChunkEvent
import com.runt9.untdrl.service.ChunkGeneratorPrototype
import com.runt9.untdrl.service.asset.EnemyMovementPrototype
import com.runt9.untdrl.service.asset.TowerAttackPrototype
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.controller.Controller
import com.runt9.untdrl.util.framework.ui.viewModel.plusAssign
import com.runt9.untdrl.view.duringRun.game.chunk.ChunkViewModel
import com.runt9.untdrl.view.duringRun.game.enemy.EnemyViewModel
import com.runt9.untdrl.view.duringRun.game.tower.TowerViewModel
import ktx.assets.async.AssetStorage
import ktx.async.onRenderingThread

class DuringRunGameController(
    private val eventBus: EventBus,
    private val assets: AssetStorage,
    private val unitMovementPrototype: EnemyMovementPrototype,
    private val towerAttackPrototype: TowerAttackPrototype,
    private val chunkGeneratorPrototype: ChunkGeneratorPrototype
) : Controller {
    private val logger = unTdRlLogger()
    override val vm = DuringRunGameViewModel()
    override val view = DuringRunGameView(this, vm, chunkGeneratorPrototype)
    private val children = mutableListOf<Controller>()

    override fun load() {
        eventBus.registerHandlers(this)
//        addNewEnemy()
//        addNewTower()
        addHomeChunk()

//        towerAttackPrototype.onProj {
//            val proj = ProjectileViewModel(1, "testProjectile", assets[UnitTexture.ENEMY.assetFile], position.cpy(), rotation)
//
//            towerAttackPrototype.onProjMove {
//                proj.position(this@onProjMove.position.cpy())
//                proj.rotation(this@onProjMove.rotation)
//            }
//
//            towerAttackPrototype.onProjDie {
//                vm.projectiles -= proj
//            }
//
//            vm.projectiles += proj
//        }
    }

    private fun addHomeChunk() {
        val chunk = ChunkViewModel(Chunk(chunkGeneratorPrototype.buildHomeChunk(), true))
        vm.chunks += chunk
    }

    private fun addNewEnemy() {
        val enemy = EnemyViewModel(1, "testEnemy", assets[UnitTexture.PLAYER.assetFile], Vector2(0f, 0f), 0f)
        
        unitMovementPrototype.onMove {
            enemy.position(position.cpy())
            enemy.rotation(rotation)
        }
        
        vm.enemies += enemy
    }

    private fun addNewTower() {
        val tower = TowerViewModel(2, "testTower", assets[UnitTexture.BOSS.assetFile], Vector2(6.125f, 5.125f), 0f)

        towerAttackPrototype.onMove {
            tower.rotation(rotation)
        }

        vm.towers += tower

        towerAttackPrototype.tower.behavior.target = unitMovementPrototype.enemy
        towerAttackPrototype.tower.target = unitMovementPrototype.enemy
    }

    @HandlesEvent(NewChunkEvent::class)
    suspend fun handleNewChunk() = onRenderingThread {
        val chunk = chunkGeneratorPrototype.generateGrid()
        vm.chunks += ChunkViewModel(Chunk(chunk))
    }

    override fun dispose() {
        eventBus.unregisterHandlers(this)
        super.dispose()
    }

    fun clearChildren() {
        children.forEach(Disposable::dispose)
        children.clear()
    }

    fun addChild(controller: Controller) = children.add(controller)
}
