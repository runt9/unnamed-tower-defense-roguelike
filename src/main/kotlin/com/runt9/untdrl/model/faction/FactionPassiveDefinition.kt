package com.runt9.untdrl.model.faction

import com.runt9.untdrl.service.factionPassiveEffect.FactionPassiveEffect
import kotlin.reflect.KClass

interface FactionPassiveDefinition {
    val name: String
    val description: String
    val effect: KClass<out FactionPassiveEffect>
}
