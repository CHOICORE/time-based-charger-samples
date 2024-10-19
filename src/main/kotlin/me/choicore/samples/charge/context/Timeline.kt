package me.choicore.samples.charge.context

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class Timeline {
    val dayOfWeek: DayOfWeek
    val specifiedDate: LocalDate?
    private val _slots: MutableList<TimeSlot> = mutableListOf()

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
        check(!this.isOverlappingWithExistingSlots(slot)) {
            "The specified time slot overlaps with an existing time slot."
        }

        this._slots.add(slot)
    }

    fun overlapsTimeSlotWith(other: Timeline): Boolean = this._slots.any { other._slots.any { other -> it.overlapsWith(other) } }

    private fun isOverlappingWithExistingSlots(slot: TimeSlot): Boolean = this._slots.any { it.overlapsWith(slot) }

    val slots: List<TimeSlot> get() = this._slots.toList()

    companion object {
        fun fullTime(date: LocalDate): Timeline = Timeline(date).apply { this.addSlot(LocalTime.MIN, LocalTime.MAX) }

        fun fullTime(dayOfWeek: DayOfWeek): Timeline = Timeline(dayOfWeek).apply { this.addSlot(LocalTime.MIN, LocalTime.MAX) }
    }
}
