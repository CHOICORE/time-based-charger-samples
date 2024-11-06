package me.choicore.samples.charge.domain

import java.time.LocalDate

interface ChargingStrategies {
    fun register(strategy: ChargingStrategy)

    fun getChargingStrategies(date: LocalDate): List<ChargingStrategy>
}
