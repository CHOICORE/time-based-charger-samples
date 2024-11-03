package me.choicore.samples.charge.temp

import java.time.LocalDate

data class TimelineChargingStrategy(
    val id: Long?,
    override val name: String,
    override val mode: ChargingMode,
    override val rate: Int,
    val timeline: Timeline,
) : ChargingStrategy {
    override fun supports(selectedDate: LocalDate): Boolean = this.timeline.satisfiedBy(selectedDate = selectedDate)
}
