package me.choicore.samples.charge.domain

import java.time.LocalDate

abstract class AbstractChargingStrategyRegistry<K> : ChargingStrategyRegistry {
    private val strategies: MutableMap<K, MutableList<ChargingStrategy>> = mutableMapOf()

    override fun register(strategy: ChargingStrategy) {
        val key: K = this.getKey(strategy = strategy)
        val existingStrategies: MutableList<ChargingStrategy> = this.strategies.getOrPut(key = key) { mutableListOf() }

        if (existingStrategies.any { it.timeline.overlapsWith(strategy.timeline) }) {
            throw IllegalArgumentException("The timeline for ${strategy.mode} overlaps with an existing strategy on $key.")
        }

        existingStrategies.add(element = strategy)
    }

    protected abstract fun getKey(strategy: ChargingStrategy): K

    override fun getChargingStrategies(date: LocalDate): List<ChargingStrategy> =
        strategies[this.getKeyForDate(date = date)] ?: this.getDefaultStrategies(date = date)

    protected abstract fun getKeyForDate(date: LocalDate): K

    protected abstract fun getDefaultStrategies(date: LocalDate): List<ChargingStrategy>
}
