package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.path.GridNode
import com.runt9.untdrl.util.framework.event.Event

class SpawnerPlacedEvent(val node: GridNode) : Event
class SpawningCompleteEvent : Event
