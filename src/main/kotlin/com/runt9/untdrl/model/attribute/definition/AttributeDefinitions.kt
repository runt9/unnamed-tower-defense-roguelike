package com.runt9.untdrl.model.attribute.definition

import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.util.ext.displayDecimal
import com.runt9.untdrl.util.ext.displayInt
import com.runt9.untdrl.util.ext.displayPercent

val amountPerInterval = attribute("Amt", "Amount per Interval", FLAT, 1f..2f) { displayInt() }
val attackSpeed = attribute("AtkSpd", "Attack Speed", PERCENT, 10f..25f) { displayDecimal() }
val costPerInterval = attribute("Cost", "Cost per Interval", FLAT, -2f..-1f) { displayInt() }
val critChance = attribute("Crit", "Crit Chance", PERCENT, 20f..50f) { (this * 100f).displayPercent() }
val critMulti = attribute("CritDmg", "Crit Multiplier", FLAT, 20f..30f) { (this * 100f).displayPercent(0) }
val damage = attribute("Dmg", "Damage", PERCENT, 10f..20f) { displayInt() }
val gainInterval = attribute("Int", "Gain Interval", PERCENT, -20f..-10f) { "${displayDecimal()}s" }
val range = attribute("Rng", "Range", PERCENT, 15f..30f) { displayDecimal(1) }
