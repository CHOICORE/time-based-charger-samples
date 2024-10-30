package me.choicore.samples.charge.domain

import java.time.LocalDate

interface ChargingStrategyRegistry {
    fun register(strategy: ChargingStrategy)

    fun getChargingStrategies(date: LocalDate): List<ChargingStrategy>
}
