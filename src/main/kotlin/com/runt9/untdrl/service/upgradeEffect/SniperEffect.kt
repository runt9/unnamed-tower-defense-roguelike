package com.runt9.untdrl.service.upgradeEffect

import com.runt9.untdrl.model.building.Building
import com.runt9.untdrl.model.building.upgrade.SniperEffectDefinition
import com.runt9.untdrl.service.buildingAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus

class SniperEffect(
    override val eventBus: EventBus,
    override val building: Building,
    private val definition: SniperEffectDefinition
) : BuildingUpgradeEffect {
    override fun apply() {
        building.attrMods += definition.modifiers
        (building.action as ProjectileAttackAction).pierce = -1
        // No need to recalculate attrs, automatically done after upgrades applied since most upgrades need it
    }
}
