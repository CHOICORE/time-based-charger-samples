package me.choicore.samples.charge.domain

import java.time.LocalDate
import java.time.LocalTime

abstract class AbstractChargingStrategyRegistry<K> : ChargingStrategyRegistry {
    private val strategies: MutableMap<K, MutableList<ChargingStrategy>> = mutableMapOf()
    private var fulfilled = false

    override fun register(strategy: ChargingStrategy) {
        val key: K = this.getKey(strategy = strategy)
        val existingStrategies: MutableList<ChargingStrategy> = this.strategies.getOrPut(key = key) { mutableListOf() }

        if (existingStrategies.any { it.timeline.overlapsWith(strategy.timeline) }) {
            throw IllegalArgumentException("The timeline for ${strategy.mode} overlaps with an existing strategy on $key.")
        }

        existingStrategies.add(element = strategy)
    }

    protected abstract fun getKey(strategy: ChargingStrategy): K

    override fun getChargingStrategies(date: LocalDate): List<ChargingStrategy> {
        val chargingStrategies = this.strategies[this.getKeyForDate(date = date)]
        if (chargingStrategies.isNullOrEmpty()) {
            return this.getDefaultStrategies(date = date)
        }
        return this.fulfilledRemainingStrategies(date = date)
    }

    private fun fulfilledRemainingStrategies(date: LocalDate): List<ChargingStrategy> {
        val chargingStrategies = this.strategies[this.getKeyForDate(date = date)]!!
        if (!this.fulfilled) {
            val existingTimeSlots = chargingStrategies.flatMap { it.timeline.slots }.sortedBy { it.startTimeInclusive }
            val remainingTimeline = Timeline(date.dayOfWeek)
            var previous = LocalTime.MIDNIGHT
            existingTimeSlots.forEach { slot ->
                if (slot.startTimeInclusive > previous) {
                    remainingTimeline.addSlot(previous, slot.startTimeInclusive)
                }
                previous = slot.endTimeInclusive
            }
            if (previous < LocalTime.MAX) {
                remainingTimeline.addSlot(previous, LocalTime.MAX)
            }
            if (remainingTimeline.slots.isNotEmpty()) {
                chargingStrategies.add(ChargingStrategy.standard(timeline = remainingTimeline))
            }
            fulfilled = true
        }
        return chargingStrategies
    }

    protected abstract fun getKeyForDate(date: LocalDate): K

    protected abstract fun getDefaultStrategies(date: LocalDate): List<ChargingStrategy>

    protected abstract class AbstractChargingStrategyRegistryBuilder<K, T : AbstractChargingStrategyRegistry<K>> {
        abstract fun build(): T
    }
}
