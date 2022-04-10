package com.runt9.untdrl.model.event

import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.util.framework.event.Event

class EnemySpawnedEvent(val enemy: Enemy) : Event
class EnemyRemovedEvent(val enemy: Enemy) : Event
