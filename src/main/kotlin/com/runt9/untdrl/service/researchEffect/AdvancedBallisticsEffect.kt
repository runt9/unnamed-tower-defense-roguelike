package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.building.intercept.beforeDamage
import com.runt9.untdrl.model.building.intercept.beforeResists
import com.runt9.untdrl.model.research.AdvancedBallisticsEffectDefinition
import com.runt9.untdrl.service.buildingAction.ProjectileAttackAction
import com.runt9.untdrl.service.duringRun.BuildingService
import com.runt9.untdrl.util.framework.event.EventBus

class AdvancedBallisticsEffect(
    override val eventBus: EventBus,
    private val definition: AdvancedBallisticsEffectDefinition,
    private val buildingService: BuildingService
) : ResearchEffect {
    private val damageInterceptor = beforeDamage { _, request ->
        request.addDamageMultiplier(definition.damagePct)
    }

    private val resistInterceptor = beforeResists { _, request ->
        request.addGlobalPenetration(definition.penetration)
    }

    override fun apply() {
        buildingService.forEachBuilding { building ->
            if (building.action !is ProjectileAttackAction) return@forEachBuilding

            building.addInterceptor(damageInterceptor)
            building.addInterceptor(resistInterceptor)
        }
    }
}
