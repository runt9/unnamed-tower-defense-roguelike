package com.runt9.untdrl.model.attribute.definition

import com.runt9.untdrl.model.attribute.AttributeModificationType.FLAT
import com.runt9.untdrl.model.attribute.AttributeModificationType.PERCENT
import com.runt9.untdrl.util.ext.displayDecimal
import com.runt9.untdrl.util.ext.displayInt
import com.runt9.untdrl.util.ext.displayMultiplier
import com.runt9.untdrl.util.ext.displayPercent

val attackSpeed = attribute("AtkSpd", "Attack Speed", PERCENT, 10f..25f) { displayDecimal() }
val critChance = attribute("Crit", "Crit Chance", PERCENT, 20f..50f) { (this * 100f).displayPercent() }
val critMulti = attribute("CritDmg", "Crit Multiplier", FLAT, 0.2f..0.3f) { displayMultiplier() }
val damage = attribute("Dmg", "Damage", PERCENT, 10f..20f) { displayInt() }
val range = attribute("Rng", "Range", PERCENT, 15f..30f) { displayDecimal(1) }
val projCount = attribute("Proj", "Projectile Count", FLAT, 1f..2f) { displayInt() }
val aoe = attribute("AoE", "Area of Effect", PERCENT, 10f..20f) { displayDecimal() }
