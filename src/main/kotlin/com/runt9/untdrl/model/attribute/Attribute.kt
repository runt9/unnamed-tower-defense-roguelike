package com.runt9.untdrl.model.attribute

import com.runt9.untdrl.model.attribute.definition.displayName
import com.runt9.untdrl.model.attribute.definition.displayValue
import com.runt9.untdrl.util.ext.FloatRange

data class Attribute(val type: AttributeType, var value: Float = 0f) {
    operator fun invoke() = value
    operator fun invoke(value: Float) {
        this.value = value
    }

    override fun toString() = "$displayName: $displayValue"
}

enum class AttributeModificationType { FLAT, PERCENT }
class AttributeRandomRange(val type: AttributeModificationType, val range: FloatRange)
