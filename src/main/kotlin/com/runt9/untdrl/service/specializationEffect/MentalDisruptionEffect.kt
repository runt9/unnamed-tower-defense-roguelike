package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.MentalDisruptionDefinition
import com.runt9.untdrl.service.towerAction.PropagandaTowerAction
import com.runt9.untdrl.service.towerAction.TowerModification
import com.runt9.untdrl.util.framework.event.EventBus

class MentalDisruptionEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: MentalDisruptionDefinition
) : TowerSpecializationEffect {
    override fun apply() {
        (tower.action as PropagandaTowerAction).apply {
            attrModification.baseModifiers = attrModification.baseModifiers.map { AttributeModifier(it.type, it.flatModifier, it.percentModifier - definition.buffReduction) }.toSet()
            modifications += AdditionalDamageTypesModifier(listOf(DamageMap(DamageType.MYSTIC, definition.damagePct)))
            recalculateModifiers()
        }
    }
    
    inner class AdditionalDamageTypesModifier(var baseDamageTypes: List<DamageMap>) : TowerModification {
        private var damageTypes = baseDamageTypes.toSet()
        private var newDamageTypes = setOf<DamageMap>()

        override fun recalculate(buffEffect: Float) {
            newDamageTypes = if (buffEffect == 0f) {
                baseDamageTypes.toSet()
            } else {
                baseDamageTypes.map { DamageMap(it.type, it.pctOfBase * (1 + buffEffect), it.penetration * (1 + buffEffect)) }.toSet()
            }
        }

        override fun applyToTower(tower: Tower) {
            tower.damageTypes += newDamageTypes
        }

        override fun removeFromTower(tower: Tower) {
            tower.damageTypes -= damageTypes
        }

        override fun finish(affectedTowers: Collection<Tower>) {
            damageTypes = newDamageTypes
        }
    }
}
