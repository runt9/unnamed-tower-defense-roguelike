package com.runt9.untdrl.service.towerAction

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.TowerPlacedEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.PropagandaTowerDefinition
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class PropagandaTowerAction(
    override val eventBus: EventBus,
    definition: PropagandaTowerDefinition,
    private val tower: Tower,
    private val towerService: TowerService
) : TowerAction {
    override suspend fun act(delta: Float) {}

    private val affectedTowers = mutableSetOf<Tower>()

    val attrModification = AttributeModifiersTowerModification(definition.modifiers.toSet())
    val modifications = mutableSetOf<TowerModification>(attrModification)

    private val towerChangeCb: Tower.() -> Unit = {
        recalculateModifiers()
    }

    override fun init() {
        recalculateModifiers()
        towerService.onTowerChange(tower.id, towerChangeCb)
        super.init()
    }

    override fun dispose() {
        affectedTowers.forEach { tower -> modifications.forEach { it.removeFromTower(tower) } }
        super.dispose()
    }

    @HandlesEvent
    suspend fun towerPlaced(event: TowerPlacedEvent) {
        val newTower = event.tower
        if (tower.inRangeOf(newTower.position)) {
            affectedTowers += newTower
            newTower.affectedByTowers += newTower
            modifications.forEach { it.applyToTower(newTower) }
            towerService.recalculateAttrs(newTower)
        }
    }

    fun recalculateModifiers() {
        val buffEffect = tower.attrs[AttributeType.BUFF_DEBUFF_EFFECT]?.invoke() ?: 0f

        modifications.forEach { it.recalculate(buffEffect) }

        val allAffectedTowers = mutableSetOf<Tower>()
        affectedTowers.forEach { t ->
            allAffectedTowers += t
            modifications.forEach { it.removeFromTower(t) }
            t.affectedByTowers -= tower
        }

        affectedTowers.clear()

        towerService.towersInRange(tower).forEach { t ->
            allAffectedTowers += t
            affectedTowers += t
            modifications.forEach { it.applyToTower(t) }
            t.affectedByTowers += tower
        }

        modifications.forEach { it.finish(allAffectedTowers) }
    }

    inner class AttributeModifiersTowerModification(var baseModifiers: Set<AttributeModifier>) : TowerModification {
        private var modifiers = baseModifiers.toSet()
        private var newModifiers = setOf<AttributeModifier>()

        override fun recalculate(buffEffect: Float) {
            newModifiers = if (buffEffect == 0f) {
                baseModifiers.toSet()
            } else {
                baseModifiers.map { AttributeModifier(it.type, it.flatModifier * (1 + buffEffect), it.percentModifier * (1 + buffEffect)) }.toSet()
            }
        }

        override fun applyToTower(tower: Tower) {
            tower.addAttributeModifiers(newModifiers)
        }

        override fun removeFromTower(tower: Tower) {
            tower.removeAttributeModifiers(modifiers)
        }

        override fun finish(affectedTowers: Collection<Tower>) {
            modifiers = newModifiers
            affectedTowers.forEach { towerService.recalculateAttrsSync(it) }
        }
    }
}

interface TowerModification {
    fun recalculate(buffEffect: Float)
    fun applyToTower(tower: Tower)
    fun removeFromTower(tower: Tower)
    fun finish(affectedTowers: Collection<Tower>)
}
