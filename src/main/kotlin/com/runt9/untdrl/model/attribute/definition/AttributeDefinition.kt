package com.runt9.untdrl.model.attribute.definition

import com.runt9.untdrl.model.attribute.Attribute
import com.runt9.untdrl.model.attribute.AttributeModificationType
import com.runt9.untdrl.model.attribute.AttributeRandomRange
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.util.ext.FloatRange

interface AttributeDefinition {
    // TODO: Convert shortName into icon
    val shortName: String
    val displayName: String
    val rangeForRandomizer: AttributeRandomRange

    fun getDisplayValue(value: Float): String
}

fun attribute(shortName: String, displayName: String, type: AttributeModificationType, range: FloatRange, displayFn: Float.() -> String) = object : AttributeDefinition {
    override val shortName = shortName
    override val displayName = displayName
    override val rangeForRandomizer = AttributeRandomRange(type, range)
    override fun getDisplayValue(value: Float) = value.displayFn()
}

val AttributeType.shortName get() = definition.shortName
val Attribute.shortName get() = type.shortName
val AttributeType.displayName get() = definition.displayName
val Attribute.displayName get() = type.displayName

fun AttributeType.getDisplayValue(value: Float) = definition.getDisplayValue(value)
val Attribute.displayValue get() = type.getDisplayValue(value)
