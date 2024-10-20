package me.choicore.samples.charge.context

import java.time.LocalDate

interface ChargingStrategyRegistry {
    fun register(strategy: ChargingStrategy)

    fun getChargingStrategies(date: LocalDate): List<ChargingStrategy>
}
