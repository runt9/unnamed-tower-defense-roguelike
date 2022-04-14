package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.util.framework.event.Event

class NewBuildingEvent(val buildingDefinition: BuildingDefinition) : Event
class BuildingPlacedEvent(val building: Building) : Event
class BuildingCancelledEvent(val building: Building) : Event
class BuildingSelectedEvent(val building: Building) : Event
