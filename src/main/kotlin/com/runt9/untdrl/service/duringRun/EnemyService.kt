package com.runt9.untdrl.service.duringRun

import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.model.attribute.AttributeType
import com.runt9.untdrl.model.damage.DamageMap
import com.runt9.untdrl.model.damage.DamageType
import com.runt9.untdrl.model.enemy.Enemy
import com.runt9.untdrl.model.enemy.status.DamagingStatusEffect
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
import com.runt9.untdrl.model.tower.intercept.OnCrit
import com.runt9.untdrl.model.tower.intercept.OnKill
import com.runt9.untdrl.model.tower.intercept.ResistanceRequest
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.util.ext.unTdRlLogger
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.event.HandlesEvent
import ktx.collections.toGdxArray
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

    val globalDamageMultipliers = mutableListOf<Float>()

    @HandlesEvent
    fun add(event: EnemySpawnedEvent) = launchOnServiceThread {
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
                        performTickDamage(se.damageSource, se.source, enemy, se.damageThisTick, listOf(DamageMap(se.damageType)), false)
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

    // TODO: NPE
    fun enemiesInRange(position: Vector2, range: Float) = enemies.toList().filter { it.isAlive && position.dst(it.position) <= range }

    fun getTowerTarget(position: Vector2, range: Float, targetingMode: TargetingMode) =
        enemiesInRange(position, range)
            .sortByTargetingMode(targetingMode)
            .firstOrNull()

    override fun stopInternal() {
        enemies.clear()
    }

    fun collidesWithEnemy(position: Vector2, maxDistance: Float) = enemies.toList().firstOrNull { it.position.dst(position) <= maxDistance }
    // TODO: NPE
    fun collidesWithEnemy(polygon: Polygon) =
        enemies.toList().firstOrNull { Intersector.intersectPolygons(it.bounds.transformedVertices.toGdxArray(), polygon.transformedVertices.toGdxArray()) }

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

    private suspend fun takeDamage(enemy: Enemy, source: Tower, finalDamage: Float) {
        val multipliers = globalDamageMultipliers.sum()
        val totalDamage = finalDamage * (1f + multipliers)

        enemy.apply {
            currentHp -= totalDamage
            affectedByTowers += source
            onHpChangeCb()
        }

        if (enemy.currentHp <= 0) {
            enemy.isAlive = false
            source.intercept(InterceptorHook.ON_KILL, OnKill(enemy))
            val allAffectedTowers = mutableSetOf<Tower>()
            enemy.affectedByTowers.forEach { t ->
                allAffectedTowers += t
                allAffectedTowers += t.affectedByTowers
            }

            allAffectedTowers.forEach { t -> towerService.gainXp(t, enemy.xpOnDeath) }
            eventBus.enqueueEvent(EnemyRemovedEvent(enemy))
        }
    }

    suspend fun attackEnemy(source: DamageSource, tower: Tower, enemyImpacted: Enemy, pointOfImpact: Vector2) {
        val enemies = if (tower.hasAttribute(AttributeType.AREA_OF_EFFECT)) enemiesInRange(pointOfImpact, tower.aoe) else mutableListOf(enemyImpacted)

        enemies.forEach { enemy ->
            if (!enemy.isAlive) return

            performFullAttack(source, tower, enemy, enemy.position.dst(pointOfImpact))
        }
    }

    private suspend fun performFullAttack(source: DamageSource, tower: Tower, enemy: Enemy, distanceFromImpact: Float) {
        var damageMultiplier = 1f
        var wasCrit = false

        if (tower.hasAttribute(AttributeType.CRIT_CHANCE)) {
            val critCheck = CritRequest(tower, enemy)
            tower.intercept(InterceptorHook.CRIT_CHECK, critCheck)
            val isCrit = randomizer.percentChance(critCheck.totalCritChance)
            if (isCrit) {
                wasCrit = true
                damageMultiplier = critCheck.totalCritMulti
                tower.intercept(InterceptorHook.ON_CRIT, OnCrit(tower, enemy))
            }
        }

        val damageRequest = damageRequest(tower, source, tower.damage, wasCrit, damageMultiplier, distanceFromImpact)
        logger.debug { "Final Damage Request: $damageRequest" }
        val damageResult = damageResult(tower, damageRequest, true)
        logger.debug { "Final Damage Result: $damageResult" }

        val resistanceRequest = resistanceRequest(tower, source, damageResult, tower.damageTypes, enemy.resistances)
        logger.debug { "Total Damage: ${resistanceRequest.finalDamage}" }
        takeDamage(enemy, tower, resistanceRequest.finalDamage)

        processProcs(tower, enemy, resistanceRequest)
    }

    fun performTickDamageSync(source: DamageSource, tower: Tower, enemy: Enemy, damageTick: Float, damageTypes: List<DamageMap>, processProcs: Boolean) = launchOnServiceThread {
        performTickDamage(source, tower, enemy, damageTick, damageTypes, processProcs)
    }

    suspend fun performTickDamage(source: DamageSource, tower: Tower, enemy: Enemy, damageTick: Float, damageTypes: List<DamageMap>, processProcs: Boolean) {
        val damageRequest = damageRequest(tower, source, damageTick)
        val damageResult = damageResult(tower, damageRequest, false)
        val resistanceRequest = resistanceRequest(tower, source, damageResult, damageTypes, enemy.resistances)
        takeDamage(enemy, tower, resistanceRequest.finalDamage)
        if (processProcs) processProcs(tower, enemy, resistanceRequest)
    }

    private fun processProcs(tower: Tower, enemy: Enemy, resistanceRequest: ResistanceRequest) =
        tower.procs.filter { randomizer.percentChance(it.chance) }.forEach { proc ->
            proc.applyToEnemy(tower, enemy, resistanceRequest)
        }

    private fun damageRequest(
        tower: Tower,
        source: DamageSource,
        damage: Float,
        wasCrit: Boolean = false,
        damageMultiplier: Float = 1f,
        distanceFromImpact: Float = 0f
    ): DamageRequest {
        val damageRequest = DamageRequest(source, damage, wasCrit, damageMultiplier, distanceFromImpact)
        tower.intercept(InterceptorHook.BEFORE_DAMAGE_CALC, damageRequest)
        return damageRequest
    }

    private fun damageResult(tower: Tower, request: DamageRequest, roll: Boolean): DamageResult {
        val result = if (roll) rollForDamage(request) else DamageResult(request.totalBaseDamage, request.totalDamageMulti)
        tower.intercept(InterceptorHook.AFTER_DAMAGE_CALC, result)
        return result
    }

    private fun resistanceRequest(tower: Tower, source: DamageSource, result: DamageResult, damageTypes: List<DamageMap>, resistances: Map<DamageType, Float>): ResistanceRequest {
        val request = ResistanceRequest(source, damageTypes.toList(), resistances.toMap(), result)
        tower.intercept(InterceptorHook.BEFORE_RESISTS, request)
        return request
    }

    private fun rollForDamage(request: DamageRequest): DamageResult {
        var damageMulti = randomizer.rng.nextInt(90, 111).toFloat() / 100f
        damageMulti *= request.totalDamageMulti
        return DamageResult(request.totalBaseDamage, damageMulti)
    }

}
