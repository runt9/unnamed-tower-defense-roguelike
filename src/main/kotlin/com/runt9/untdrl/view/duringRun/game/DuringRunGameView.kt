package com.runt9.untdrl.view.duringRun.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.service.ChunkGeneratorPrototype
import com.runt9.untdrl.util.ext.ui.rectPixmapTexture
import com.runt9.untdrl.util.ext.ui.toDrawable
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.ui.view.TableView
import com.runt9.untdrl.view.duringRun.CHUNK_SIZE
import com.runt9.untdrl.view.duringRun.GAME_HEIGHT
import com.runt9.untdrl.view.duringRun.GAME_WIDTH
import com.runt9.untdrl.view.duringRun.game.enemy.enemy
import com.runt9.untdrl.view.duringRun.game.projectile.projectile
import com.runt9.untdrl.view.duringRun.game.tower.tower
import ktx.scene2d.table
import ktx.scene2d.vis.floatingGroup
import ktx.scene2d.vis.visTable

class DuringRunGameView(
    override val controller: DuringRunGameController,
    override val vm: DuringRunGameViewModel,
    private val chunkGeneratorPrototype: ChunkGeneratorPrototype
) : TableView(controller, vm) {
    private val logger = unTdRlLogger()

    private val path = listOf(
        Vector2(0f, 1f),
        Vector2(0f, 2f),
        Vector2(0f, 3f),
        Vector2(1f, 3f),
        Vector2(2f, 3f),
        Vector2(3f, 3f),
        Vector2(4f, 3f),
        Vector2(4f, 4f),
        Vector2(5f, 4f),
        Vector2(6f, 4f),
        Vector2(7f, 4f),
        Vector2(7f, 5f),
        Vector2(7f, 6f),
        Vector2(7f, 7f),
    )

    override fun init() {
        val vm = vm
        val controller = controller

        setSize(GAME_WIDTH, GAME_HEIGHT)
//        centerPosition()

        floatingGroup {
            visTable {
                this@DuringRunGameView.chunkGeneratorPrototype.buildHomeChunk().reversedArray().forEach { row ->
                    row.forEach { col ->
                        val color = when (col) {
                            1 -> Color.WHITE
                            2 -> Color.GREEN
                            3 -> Color.RED
                            else -> Color.DARK_GRAY
                        }
                        table {
                            background(rectPixmapTexture(1, 1, color).toDrawable())
                        }.cell(grow = true)
                    }
                    row()
                }

                setSize(CHUNK_SIZE, CHUNK_SIZE)
            }

            vm.enemies.bind {
                this@DuringRunGameView.logger.info { "Updating all enemies" }
//                clear()
//                controller.clearChildren()
                vm.enemies.get().forEach { enemy -> enemy(enemy) { controller.addChild(this.controller) } }
            }

            vm.towers.bind {
                this@DuringRunGameView.logger.info { "Updating all towers" }
//                clear()
//                controller.clearChildren()
                vm.towers.get().forEach { tower -> tower(tower) { controller.addChild(this.controller) } }
            }

            vm.projectiles.bind {
                this@DuringRunGameView.logger.info { "Updating all projectiles" }
//                clear()
//                controller.clearChildren()
                vm.projectiles.get().forEach { projectile -> projectile(projectile) { controller.addChild(this.controller) } }
            }
        }.cell(row = true, grow = true)

        debugAll()
    }
}
