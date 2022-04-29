package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.specialization.AttributeModifiersSpecialization
import com.runt9.untdrl.util.framework.event.EventBus

class AttributeModifiersSpecializationEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: AttributeModifiersSpecialization
) : TowerSpecializationEffect {
    override fun apply() {
        tower.attrMods += definition.modifiers
        // No need to recalculate attrs, automatically done after specializations applied since most specializations need it
    }
}
