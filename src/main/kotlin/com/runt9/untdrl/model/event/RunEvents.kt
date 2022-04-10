package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.RunState
import com.runt9.untdrl.util.framework.event.Event
import com.runt9.untdrl.util.framework.event.EventBus

class RunStateUpdated(val newState: RunState) : Event

class GamePauseChanged(val isPaused: Boolean) : Event
fun EventBus.pauseGame() = enqueueEventSync(GamePauseChanged(true))
fun EventBus.resumeGame() = enqueueEventSync(GamePauseChanged(false))

class WaveStartedEvent : Event
class WaveCompleteEvent : Event
class PrepareNextWaveEvent : Event
