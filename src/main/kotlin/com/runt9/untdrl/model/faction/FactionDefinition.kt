package com.runt9.untdrl.model.faction

import com.runt9.untdrl.model.research.ResearchDefinition
import com.runt9.untdrl.model.tower.definition.TowerDefinition
import com.runt9.untdrl.service.factionPassiveEffect.FactionPassiveEffect
import kotlin.reflect.KClass

interface FactionDefinition {
    val id: Int
    val name: String
    val maxHp: Int
    val startingTower: TowerDefinition
    val goldPassive: FactionPassiveDefinition
    val researchPassive: FactionPassiveDefinition
    val research: List<ResearchDefinition>

    class Builder {
        internal lateinit var startingTower: TowerDefinition
        internal lateinit var goldPassive: FactionPassiveDefinition
        internal lateinit var researchPassive: FactionPassiveDefinition
        val research = mutableListOf<ResearchDefinition>()

        fun startingTower(tower: TowerDefinition) {
            startingTower = tower
        }

        fun goldPassive(name: String, effectClass: KClass<out FactionPassiveEffect>, builder: PassiveBuilder.() -> Unit = {}) {
            goldPassive = passive(name, effectClass, builder)
        }

        fun researchPassive(name: String, effectClass: KClass<out FactionPassiveEffect>, builder: PassiveBuilder.() -> Unit = {}) {
            researchPassive = passive(name, effectClass, builder)
        }

        private fun passive(name: String, effectClass: KClass<out FactionPassiveEffect>, builder: PassiveBuilder.() -> Unit = {}): FactionPassiveDefinition {
            val passiveBuilder = PassiveBuilder()
            passiveBuilder.builder()

            return object : FactionPassiveDefinition {
                override val name = name
                override val description = passiveBuilder.description
                override val effect = effectClass
            }
        }

        class PassiveBuilder {
            internal lateinit var description: String

            operator fun String.unaryPlus() {
                description = this
            }
        }
    }
}

fun faction(
    id: Int,
    name: String,
    maxHp: Int,
    init: FactionDefinition.Builder.() -> Unit
): FactionDefinition {
    val builder = FactionDefinition.Builder()
    builder.init()

    return object : FactionDefinition {
        override val id = id
        override val name = name
        override val maxHp = maxHp
        override val startingTower = builder.startingTower
        override val goldPassive = builder.goldPassive
        override val researchPassive = builder.researchPassive
        override val research = builder.research
    }
}
