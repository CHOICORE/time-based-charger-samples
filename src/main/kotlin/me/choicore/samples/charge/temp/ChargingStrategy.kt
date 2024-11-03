package me.choicore.samples.charge.temp

import java.time.LocalDate

interface ChargingStrategy {
    val name: String
    val mode: ChargingMode
    val rate: Int

    fun supports(selectedDate: LocalDate): Boolean
}
