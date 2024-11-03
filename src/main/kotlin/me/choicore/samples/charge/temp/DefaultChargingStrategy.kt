package me.choicore.samples.charge.temp

import java.time.DayOfWeek
import java.time.LocalDate
import java.util.EnumSet

data class DefaultChargingStrategy(
    override val name: String,
    override val mode: ChargingMode,
    override val rate: Int,
    val daysOfWeek: Set<DayOfWeek>? = EnumSet.allOf(DayOfWeek::class.java),
) : ChargingStrategy {
    override fun supports(selectedDate: LocalDate): Boolean = this.daysOfWeek?.contains(selectedDate.dayOfWeek) ?: false
}
