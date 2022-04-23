package com.runt9.untdrl.service.consumableAction

interface ConsumableAction {
    fun canApply(): Boolean
    fun apply()
}
