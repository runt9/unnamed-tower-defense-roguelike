package com.runt9.untdrl.service.specializationEffect

import com.runt9.untdrl.model.attribute.AttributeModifier
import com.runt9.untdrl.model.attribute.AttributeType.ATTACK_SPEED
import com.runt9.untdrl.model.attribute.AttributeType.DAMAGE
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.definition.MinigunSpecialization
import com.runt9.untdrl.model.tower.intercept.onAttack
import com.runt9.untdrl.service.duringRun.Ticker
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
    private val decayTimer = Timer(2f)

    val tick: Ticker = { delta ->
        decayTimer.tick(delta)
        if (decayTimer.isReady) {
            decayStack()
            decayTimer.reset(false)
        }
    }

    private fun decayStack() {
        if (modifierStacks.isEmpty()) return
        val modifier = modifierStacks.removeLast()
        tower.attrMods.remove(modifier)
        towerService.recalculateAttrsSync(tower)
        currentAttackSpeedBoost -= attackSpeedBoostPerShot
    }

    override fun init() {
        tickerRegistry.registerTicker(tick)
        super.init()
    }

    override fun apply() {
        tower.modifyBaseAndLevelGrowth(DAMAGE, percentModifier = -definition.attributeReduction)
        tower.modifyBaseAndLevelGrowth(ATTACK_SPEED, percentModifier = -definition.attributeReduction)

        tower.addInterceptor(onAttack { _, _ ->
            decayTimer.reset(false)
            if (currentAttackSpeedBoost == maxAttackSpeedBoost) return@onAttack

            currentAttackSpeedBoost += attackSpeedBoostPerShot
            val newModifier = AttributeModifier(ATTACK_SPEED, percentModifier = attackSpeedBoostPerShot, isTemporary = true)
            modifierStacks += newModifier
            tower.attrMods += newModifier
            towerService.recalculateAttrsSync(tower)
        })
    }

    override fun dispose() {
        tickerRegistry.unregisterTicker(tick)
        super.dispose()
    }

    @HandlesEvent(WaveCompleteEvent::class)
    fun waveComplete() {
        decayTimer.reset(false)
        towerService.recalculateAttrsSync(tower)
        currentAttackSpeedBoost = 0f
    }
}
