package me.choicore.samples.charge.context

import java.time.LocalTime

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
    fun overlapsWith(other: TimeSlot): Boolean = startTimeInclusive < other.endTimeInclusive && endTimeInclusive > other.startTimeInclusive

    fun extractWithin(
        startTimeInclusive: LocalTime,
        endTimeInclusive: LocalTime,
    ): TimeSlot? {
        if (this.isFullTime()) {
            return TimeSlot(startTimeInclusive, endTimeInclusive)
        } else {
            val start =
                if (this.startTimeInclusive.isAfter(startTimeInclusive)) this.startTimeInclusive else startTimeInclusive
            val end =
                if (this.endTimeInclusive.isBefore(endTimeInclusive)) this.endTimeInclusive else endTimeInclusive
            return if (start.isBefore(end)) TimeSlot(start, end) else null
        }
    }

    fun isFullTime(): Boolean = this.startTimeInclusive == LocalTime.MIN && this.endTimeInclusive == LocalTime.MAX
}
