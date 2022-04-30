package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.MinigunSpecialization
import com.runt9.untdrl.model.tower.intercept.onAttack
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.ext.Timer
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent

class MinigunEffect(
    override val eventBus: EventBus,
    override val tower: Tower,
    private val definition: MinigunSpecialization,
    private val towerService: TowerService,
    private val tickerRegistry: TickerRegistry
) : TowerSpecializationEffect {
    private val maxAttackSpeedBoost = definition.maxAttackSpeedBoost
    private val attackSpeedBoostPerShot = definition.attackSpeedBoostPerShot
    private var currentAttackSpeedBoost = 0f
    private val modifierStacks = mutableListOf<AttributeModifier>()
    private val decayTimer = Timer(1f)

    val tick: (Float) -> Unit = { _ ->
        if (decayTimer.isReady) {
            val modifier = modifierStacks.removeLast()
            tower.attrMods.remove(modifier)
            towerService.recalculateAttrsSync(tower)
            decayTimer.reset(false)
            currentAttackSpeedBoost -= attackSpeedBoostPerShot
        }
    }

    override fun init() {
        tickerRegistry.registerTicker(tick)
        super.init()
    }

    override fun apply() {
        tower.attrMods += AttributeModifier(AttributeType.DAMAGE, percentModifier = -definition.attributeReduction)
        tower.attrMods += AttributeModifier(AttributeType.ATTACK_SPEED, flatModifier = -0.25f)

        tower.addInterceptor(onAttack { _, _ ->
            if (currentAttackSpeedBoost == maxAttackSpeedBoost) return@onAttack

            currentAttackSpeedBoost += attackSpeedBoostPerShot
            val newModifier = AttributeModifier(AttributeType.ATTACK_SPEED, percentModifier = attackSpeedBoostPerShot)
            modifierStacks += newModifier
            tower.attrMods += newModifier
            towerService.recalculateAttrsSync(tower)
            decayTimer.reset(false)
        })
    }

    override fun dispose() {
        tickerRegistry.unregisterTicker(tick)
        super.dispose()
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        decayTimer.reset(false)
        modifierStacks.forEach { tower.attrMods.remove(it) }
        towerService.recalculateAttrsSync(tower)
        currentAttackSpeedBoost = 0f
    }
}