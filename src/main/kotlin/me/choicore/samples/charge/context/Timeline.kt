package me.choicore.samples.charge.context

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class Timeline {
    val dayOfWeek: DayOfWeek
    val specifiedDate: LocalDate?

    constructor(dayOfWeek: DayOfWeek) {
        this.dayOfWeek = dayOfWeek
        this.specifiedDate = null
    }

    constructor(specifyDate: LocalDate) {
        this.dayOfWeek = specifyDate.dayOfWeek
        this.specifiedDate = specifyDate
    }

    fun satisfiedBy(date: LocalDate): Boolean = this.specifiedDate?.isEqual(date) ?: (this.dayOfWeek == date.dayOfWeek)

    fun addSlot(
        startTimeInclusive: LocalTime,
        endTimeInclusive: LocalTime,
    ) {
        this.addSlot(TimeSlot(startTimeInclusive, endTimeInclusive))
    }

    fun addSlot(slot: TimeSlot) {
        if (this.isOverlappingWithExistingSlots(slot)) {
            throw IllegalArgumentException("The specified time slot overlaps with an existing time slot.")
        }
        this._slots.add(slot)
    }

    fun overlapsTimeSlotWith(other: Timeline): Boolean =
        this._slots.any { slot -> other.slots.any { it.overlapsWith(slot) } }

    private fun isOverlappingWithExistingSlots(slot: TimeSlot): Boolean = this._slots.any { it.overlapsWith(slot) }

    private val _slots = mutableListOf<TimeSlot>()

    val slots: List<TimeSlot>
        get() = this._slots.toList()

    /**
     * Represents a time slot within a day, defined by a start and end time.
     *
     * The time slot is inclusive for both the start and end times, meaning that
     * the specified start and end times are considered part of the time range.
     *
     * Usage examples:
     *
     * Correct usage:
     * ```
     * // Defines a time slot from 09:00 to 17:00, where both times are inclusive.
     * val timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))
     * ```
     *
     * Incorrect usage (throws IllegalArgumentException):
     * ```
     * // This will throw an exception because the start time is after the end time.
     * val timeSlot = TimeSlot(LocalTime.of(17, 0), LocalTime.of(9, 0))
     * ```
     *
     * @property startTimeInclusive The start time of the time slot, inclusive.
     * @property endTimeInclusive The end time of the time slot, inclusive.
     *
     * @throws IllegalArgumentException If the start time is after the end time.
     */
    data class TimeSlot(
        val startTimeInclusive: LocalTime,
        val endTimeInclusive: LocalTime,
    ) {
        init {
            require(this.startTimeInclusive.isBefore(this.endTimeInclusive)) {
                "startTimeInclusive must be before endTimeInclusive"
            }
        }

        /**
         * Determines whether this time slot overlaps with another time slot.
         *
         * Two time slots are considered overlapping if there is any time that falls within both slots.
         *
         * @param other The other [TimeSlot] to check for overlap.
         * @return `true` if the time slots overlap, `false` otherwise.
         */
        fun overlapsWith(other: TimeSlot): Boolean =
            this.startTimeInclusive < other.endTimeInclusive && this.endTimeInclusive > other.startTimeInclusive
    }
}
