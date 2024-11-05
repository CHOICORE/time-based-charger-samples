package me.choicore.samples.charge.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class ChargingStrategy(
    val id: Long? = null,
    val mode: ChargingMode,
    val rate: Int,
    val timeline: Timeline,
) {
    val dayOfWeek: DayOfWeek = this.timeline.dayOfWeek
    val specifiedDate: LocalDate? = this.timeline.specifiedDate
    val cycle: Cycle = if (this.specifiedDate != null) Cycle.ONCE else Cycle.REPEATABLE

    fun supports(selectedDate: LocalDate): Boolean = this.timeline.satisfiedBy(selectedDate = selectedDate)

    fun attempt(chargingUnit: ChargingUnit) {
        require(value = this.supports(selectedDate = chargingUnit.chargedOn)) { "The specified date does not satisfy the timeline." }
        chargingUnit.adjust(strategy = this)
    }

    enum class Cycle {
        REPEATABLE,
        ONCE,
    }

    companion object {
        fun standard(dayOfWeek: DayOfWeek): ChargingStrategy =
            ChargingStrategy(
                mode = ChargingMode.NONE,
                rate = 100,
                timeline = Timeline.fullTime(dayOfWeek = dayOfWeek),
            )

        fun standard(timeline: Timeline): ChargingStrategy =
            ChargingStrategy(
                mode = ChargingMode.NONE,
                rate = 100,
                timeline = timeline,
            )

        fun exempt(dayOfWeek: DayOfWeek): ChargingStrategy =
            ChargingStrategy(
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline = Timeline.fullTime(dayOfWeek = dayOfWeek),
            )

        fun exempt(timeline: Timeline) {
            ChargingStrategy(
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline = timeline,
            )
        }
    }
}
