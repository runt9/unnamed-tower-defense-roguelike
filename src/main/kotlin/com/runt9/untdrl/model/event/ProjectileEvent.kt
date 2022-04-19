package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.building.Projectile
import com.runt9.untdrl.util.framework.event.Event

class ProjectileSpawnedEvent(val projectile: Projectile) : Event
