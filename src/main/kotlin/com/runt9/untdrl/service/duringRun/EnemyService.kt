package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.enemy.DamagingStatusEffect
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.event.EnemyRemovedEvent
import com.runt9.untdrl.model.event.EnemySpawnedEvent
import com.runt9.untdrl.model.event.SpawningCompleteEvent
import com.runt9.untdrl.model.event.WaveCompleteEvent
import com.runt9.untdrl.model.tower.TargetingMode
import com.runt9.untdrl.model.tower.Tower
import com.runt9.untdrl.model.tower.aoe
import com.runt9.untdrl.model.tower.damage
import com.runt9.untdrl.model.tower.intercept.CritRequest
import com.runt9.untdrl.model.tower.intercept.DamageRequest
import com.runt9.untdrl.model.tower.intercept.DamageResult
import com.runt9.untdrl.model.tower.intercept.DamageSource
import com.runt9.untdrl.model.tower.intercept.InterceptorHook
import com.runt9.untdrl.model.tower.intercept.ResistanceRequest
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import kotlin.math.roundToInt

class EnemyService(
    private val grid: IndexedGridGraph,
    private val eventBus: EventBus,
    registry: RunServiceRegistry,
    private val towerService: TowerService,
    private val randomizer: RandomizerService
) : RunService(eventBus, registry) {
    private val logger = unTdRlLogger()
    private val enemies = mutableListOf<Enemy>()
    private var isSpawning = false

    @HandlesEvent
    fun add(event: EnemySpawnedEvent) = launchOnServiceThread {
        // TODO: Make sure this works and there's no weird race condition. Might want to use start wave event instead
        isSpawning = true
        enemies += event.enemy
    }

    @HandlesEvent
    suspend fun remove(event: EnemyRemovedEvent) = launchOnServiceThread {
        enemies -= event.enemy

        if (!isSpawning && enemies.isEmpty()) {
            eventBus.enqueueEvent(WaveCompleteEvent())
        }
    }

    @HandlesEvent(SpawningCompleteEvent::class)
    fun spawningComplete() {
        isSpawning = false
    }

    override fun tick(delta: Float) {
        launchOnServiceThread {
            enemies.toList().filter { it.isAlive }.forEach { enemy ->
                enemy.statusEffects.toList().forEach { se ->
                    se.tick(delta)

                    if (se is DamagingStatusEffect) {
                        performDotDamage(se.damageSource, se.source, enemy, se.damageThisTick, se.damageType)
                    }

                    if (se.timer.isReady) {
                        enemy.statusEffects.remove(se)
                    }
                }

                val steeringOutput = SteeringAcceleration(Vector2())
                enemy.behavior.calculateSteering(steeringOutput)
                if (!steeringOutput.isZero) {
                    enemy.applySteering(delta, steeringOutput)

                    if (enemy.position.dst(grid.home.point).roundToInt() == 0) {
                        logger.info { "${enemy.id}: Enemy hit home" }
                        enemies -= enemy
                        eventBus.enqueueEventSync(EnemyRemovedEvent(enemy, false))
                    }
                }
            }
        }
    }

    fun getTowerTarget(position: Vector2, range: Float, targetingMode: TargetingMode) =
        enemies.toList()
            .sortByTargetingMode(targetingMode)
            .filter { it.isAlive }
            .find { enemy ->
                position.dst(enemy.position) <= range
            }

    override fun stopInternal() {
        enemies.clear()
    }

    fun collidesWithEnemy(position: Vector2, maxDistance: Float) = enemies.toList().firstOrNull { it.position.dst(position) <= maxDistance }

    private fun List<Enemy>.sortByTargetingMode(targetingMode: TargetingMode) = when(targetingMode) {
        TargetingMode.FRONT -> sortedBy { it.numNodesToHome() }
        TargetingMode.BACK -> sortedByDescending { it.numNodesToHome() }
        TargetingMode.STRONG -> sortedByDescending { it.maxHp }
        TargetingMode.WEAK -> sortedBy { it.maxHp }
        TargetingMode.FAST -> sortedByDescending { it.linearSpeedLimit }
        TargetingMode.SLOW -> sortedBy { it.linearSpeedLimit }
        TargetingMode.NEAR_DEATH -> sortedBy { it.currentHp }
        TargetingMode.HEALTHIEST -> sortedByDescending { it.currentHp }
    }

    private fun enemiesInRange(fromPosition: Vector2, range: Float) = enemies.filter { it.position.dst(fromPosition) <= range }

    private suspend fun takeDamage(enemy: Enemy, source: Tower, finalDamage: Float) {
        enemy.apply {
            currentHp -= finalDamage
            affectedByTowers += source
            onHpChangeCb()
        }

        if (enemy.currentHp <= 0) {
            enemy.isAlive = false
            enemy.affectedByTowers.forEach { t -> towerService.gainXp(t, enemy.xpOnDeath) }
            eventBus.enqueueEvent(EnemyRemovedEvent(enemy))
        }
    }

    suspend fun attackEnemy(source: DamageSource, tower: Tower, enemyImpacted: Enemy, pointOfImpact: Vector2) {
        val enemies = if (tower.hasAttribute(AttributeType.AREA_OF_EFFECT)) enemiesInRange(pointOfImpact, tower.aoe) else mutableListOf(enemyImpacted)

        enemies.forEach { enemy ->
            if (!enemy.isAlive) return

            performFullAttack(source, tower, enemy)
        }
    }

    private suspend fun performFullAttack(source: DamageSource, tower: Tower, enemy: Enemy) {
        var damageMultiplier = 1f

        if (tower.hasAttribute(AttributeType.CRIT_CHANCE)) {
            val critCheck = CritRequest(tower)
            tower.intercept(InterceptorHook.CRIT_CHECK, critCheck)
            val isCrit = randomizer.percentChance(critCheck.totalCritChance)
            if (isCrit) damageMultiplier = critCheck.totalCritMulti
        }

        val damageRequest = DamageRequest(source, tower.damage, damageMultiplier)
        tower.intercept(InterceptorHook.BEFORE_DAMAGE_CALC, damageRequest)
        logger.info { "Final Damage Request: $damageRequest" }
        val damageResult = rollForDamage(damageRequest)
        tower.intercept(InterceptorHook.AFTER_DAMAGE_CALC, damageResult)
        logger.info { "Final Damage Result: $damageResult" }
        val resistanceRequest = ResistanceRequest(source, tower.damageTypes.toList(), enemy.resistances.toMap(), damageResult)
        tower.intercept(InterceptorHook.BEFORE_RESISTS, resistanceRequest)
        logger.info { "Total Damage: ${resistanceRequest.finalDamage}" }
        takeDamage(enemy, tower, resistanceRequest.finalDamage)

        tower.procs.filter { randomizer.percentChance(it.chance) }.forEach { proc -> proc.applyToEnemy(tower, enemy, resistanceRequest.finalDamage) }
    }

    private suspend fun performDotDamage(source: DamageSource, tower: Tower, enemy: Enemy, damageTick: Float, damageType: DamageType) {
        val damageRequest = DamageRequest(source, damageTick)
        tower.intercept(InterceptorHook.BEFORE_DAMAGE_CALC, damageRequest)

        val damageResult = DamageResult(damageRequest.totalBaseDamage, damageRequest.totalDamageMulti)
        tower.intercept(InterceptorHook.AFTER_DAMAGE_CALC, damageResult)

        val resistanceRequest = ResistanceRequest(source, listOf(DamageMap(damageType)), enemy.resistances.toMap(), damageResult)
        tower.intercept(InterceptorHook.BEFORE_RESISTS, resistanceRequest)
        takeDamage(enemy, tower, resistanceRequest.finalDamage)
    }

    private fun rollForDamage(request: DamageRequest): DamageResult {
        var damageMulti = randomizer.rng.nextInt(90, 111).toFloat() / 100f
        damageMulti *= request.totalDamageMulti
        return DamageResult(request.totalBaseDamage, damageMulti)
    }

}
