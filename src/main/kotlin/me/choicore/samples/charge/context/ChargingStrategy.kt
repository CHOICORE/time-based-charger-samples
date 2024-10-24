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

    fun supports(date: LocalDate): Boolean = this.timeline.satisfiedBy(date = date)

    fun audit(charge: Charge) {
        require(this.supports(charge.date)) { "The specified date does not satisfy the timeline." }

        this.timeline.slots.forEach { slot ->
            val basis: TimeSlot = slot
            val applied: TimeSlot? = slot.extractWithin(charge.start, charge.end)
            if (applied != null) {
                charge.adjust(
                    mode = this.mode,
                    rate = this.rate,
                    basis = basis,
                    applied = applied,
                )
            }
        }
    }

    companion object {
        fun noop(dayOfWeek: DayOfWeek): ChargingStrategy =
            ChargingStrategy(
                mode = ChargingMode.STANDARD,
                rate = 100,
                timeline = Timeline.fullTime(dayOfWeek = dayOfWeek),
            )
    }

    enum class Type {
        REPEATABLE,
        ONCE,
    }
}
