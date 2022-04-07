package com.runt9.untdrl.view.duringRun

import com.badlogic.gdx.ai.Timepiece
import com.runt9.untdrl.service.asset.EnemyMovementPrototype
import com.runt9.untdrl.service.asset.TowerAttackPrototype
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.core.GameScreen
import com.runt9.untdrl.view.duringRun.game.DuringRunGameController
import com.runt9.untdrl.view.duringRun.ui.DuringRunUiController

class DuringRunScreen(
    override val gameController: DuringRunGameController,
    override val uiController: DuringRunUiController,
    private val eventBus: EventBus,
    private val aiTimepiece: Timepiece,
    private val unitMovementPrototype: EnemyMovementPrototype,
    private val towerAttackPrototype: TowerAttackPrototype
) : GameScreen(GAME_AREA_WIDTH, GAME_AREA_HEIGHT) {
    private var isRunning = true
    private var isPaused = false

    override fun show() {
        eventBus.registerHandlers(this)
        super.show()
    }

    override fun render(delta: Float) {
        if (isRunning && !isPaused) {
            aiTimepiece.update(delta)
            unitMovementPrototype.tick(delta)
            towerAttackPrototype.tick(delta)
        }
        super.render(delta)
    }

    override fun hide() {
        eventBus.unregisterHandlers(this)
        isRunning = false
        isPaused = false
        super.hide()
    }
}
