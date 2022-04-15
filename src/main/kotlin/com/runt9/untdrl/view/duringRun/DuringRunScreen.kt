package com.runt9.untdrl.view.duringRun

import com.badlogic.gdx.ai.Timepiece
import com.runt9.untdrl.model.event.GamePauseChanged
import com.runt9.untdrl.model.event.RunEndEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.event.WaveStartedEvent
import com.runt9.untdrl.model.event.enqueueShowDialog
import com.runt9.untdrl.service.duringRun.RunInitializer
import com.runt9.untdrl.service.duringRun.RunServiceRegistry
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import com.runt9.untdrl.util.framework.ui.core.GameScreen
import com.runt9.untdrl.view.duringRun.game.DuringRunGameController
import com.runt9.untdrl.view.duringRun.ui.DuringRunUiController
import com.runt9.untdrl.view.duringRun.ui.runEnd.RunEndDialogController
import ktx.async.onRenderingThread

class DuringRunScreen(
    override val gameController: DuringRunGameController,
    override val uiController: DuringRunUiController,
    private val eventBus: EventBus,
    private val aiTimepiece: Timepiece,
    private val runInitializer: RunInitializer,
    private val runServiceRegistry: RunServiceRegistry,
    private val inputHandler: DuringRunInputController
) : GameScreen(GAME_AREA_WIDTH, GAME_AREA_HEIGHT) {
    private var isRunning = false
    private var isPaused = false

    @HandlesEvent(WaveStartedEvent::class) suspend fun waveStart() = onRenderingThread { isRunning = true }
    @HandlesEvent(WaveCompleteEvent::class) suspend fun waveComplete() = onRenderingThread { isRunning = false }

    @HandlesEvent
    suspend fun pauseResume(event: GamePauseChanged) = onRenderingThread { isPaused = event.isPaused }

    @HandlesEvent
    suspend fun runEnd(event: RunEndEvent) = onRenderingThread {
        // TODO: Need a proper clear of everything
        isPaused = true
        eventBus.enqueueShowDialog<RunEndDialogController>()
    }

    override fun show() {
        eventBus.registerHandlers(this)
        input.addProcessor(inputHandler)
        runInitializer.initialize()
        super.show()
    }

    override fun render(delta: Float) {
        if (isRunning && !isPaused) {
            aiTimepiece.update(delta)
            runServiceRegistry.tickAll(delta)
        }

        super.render(delta)
    }

    override fun hide() {
        eventBus.unregisterHandlers(this)
        input.removeProcessor(inputHandler)
        runInitializer.dispose()
        isRunning = false
        isPaused = false
        super.hide()
    }
}
