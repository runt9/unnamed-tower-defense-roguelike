package com.runt9.untdrl.service.researchEffect

import com.runt9.untdrl.model.research.AdvancedBallisticsEffectDefinition
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.intercept.beforeDamage
import com.runt9.untdrl.model.tower.intercept.beforeResists
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.service.towerAction.ProjectileAttackAction
import com.runt9.untdrl.util.framework.event.EventBus

class AdvancedBallisticsEffect(
    override val eventBus: EventBus,
    private val definition: AdvancedBallisticsEffectDefinition,
    private val towerService: TowerService
) : ResearchEffect {
    private val damageInterceptor = beforeDamage(DamageSource.PROJECTILE) { _, request ->
        request.addDamageMultiplier(definition.damagePct)
    }

    private val resistInterceptor = beforeResists(DamageSource.PROJECTILE) { _, request ->
        if (request.source != DamageSource.PROJECTILE) return@beforeResists
        request.addGlobalPenetration(definition.penetration)
    }

    override fun apply() {
        towerService.forEachTower { tower ->
            if (tower.action !is ProjectileAttackAction) return@forEachTower

            tower.addInterceptor(damageInterceptor)
            tower.addInterceptor(resistInterceptor)
        }
    }
}
