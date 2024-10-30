package me.choicore.samples.charge.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class ChargingStrategy(
    val mode: ChargingMode,
    val rate: Int,
    val timeline: Timeline,
) {
    val dayOfWeek: DayOfWeek = this.timeline.dayOfWeek
    val specifiedDate: LocalDate? = this.timeline.specifiedDate

    val type: Type = if (this.specifiedDate != null) Type.ONCE else Type.REPEATABLE

    fun supports(selectedDate: LocalDate): Boolean = this.timeline.satisfiedBy(selectedDate = selectedDate)

    fun attempt(chargingUnit: ChargingUnit) {
        require(value = this.supports(selectedDate = chargingUnit.chargedOn)) { "The specified date does not satisfy the timeline." }
        chargingUnit.adjust(strategy = this)
    }

    enum class Type {
        REPEATABLE,
        ONCE,
    }

    companion object {
        fun noop(dayOfWeek: DayOfWeek): ChargingStrategy =
            ChargingStrategy(
                mode = ChargingMode.STANDARD,
                rate = 100,
                timeline = Timeline.fullTime(dayOfWeek = dayOfWeek),
            )

        fun exempt(dayOfWeek: DayOfWeek): ChargingStrategy =
            ChargingStrategy(
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline = Timeline.fullTime(dayOfWeek = dayOfWeek),
            )
    }
}
