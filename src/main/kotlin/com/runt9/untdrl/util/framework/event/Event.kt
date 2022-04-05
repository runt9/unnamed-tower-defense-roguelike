package com.runt9.untdrl.util.framework.event

interface Event {
    val name: String get() = this::class.simpleName ?: "Event"
}
