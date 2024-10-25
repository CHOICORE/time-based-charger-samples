package me.choicore.samples.charge.context

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

    fun attempt(charge: Charge) {
        require(value = this.supports(selectedDate = charge.date)) { "The specified date does not satisfy the timeline." }
        charge.adjust(strategy = this)
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
    }
}
