package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.Chunk
import com.runt9.untdrl.util.framework.event.Event

class NewChunkEvent : Event
class ChunkPlacedEvent(val chunk: Chunk) : Event
class ChunkCancelledEvent(val chunk: Chunk) : Event
