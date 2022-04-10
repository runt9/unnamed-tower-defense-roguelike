package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.util.framework.event.Event

class NewTowerEvent(val towerDefinition: TowerDefinition) : Event
class TowerPlacedEvent(val tower: Tower) : Event
class TowerCancelledEvent(val tower: Tower) : Event
