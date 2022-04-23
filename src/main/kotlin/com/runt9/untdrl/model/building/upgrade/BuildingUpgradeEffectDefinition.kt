package com.runt9.untdrl.model.building.upgrade

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.building.definition.BuildingDefinition
import com.runt9.untdrl.service.upgradeEffect.BuildingUpgradeEffect
import com.runt9.untdrl.service.upgradeEffect.SniperEffect
import kotlin.reflect.KClass

interface BuildingUpgradeEffectDefinition {
    val effectClass: KClass<out BuildingUpgradeEffect>
}

class SniperEffectDefinition(val modifiers: List<AttributeModifier>) : BuildingUpgradeEffectDefinition {
    override val effectClass = SniperEffect::class
}

fun BuildingDefinition.Builder.UpgradeBuilder.sniperEffect(vararg modifiers: AttributeModifier) {
    definition = SniperEffectDefinition(modifiers.toList())
}
