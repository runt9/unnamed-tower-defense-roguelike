package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.Tower
import com.runt9.untdrl.util.framework.event.Event

class NewTowerEvent : Event
class TowerPlacedEvent(val tower: Tower) : Event
