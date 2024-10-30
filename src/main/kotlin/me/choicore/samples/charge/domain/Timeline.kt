package me.choicore.samples.charge.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class Timeline {
    val dayOfWeek: DayOfWeek
    val specifiedDate: LocalDate?
    private val _slots: MutableList<TimeSlot> = mutableListOf()
    val slots: List<TimeSlot> get() = this._slots.toList()

    constructor(dayOfWeek: DayOfWeek) {
        this.dayOfWeek = dayOfWeek
        this.specifiedDate = null
    }

    constructor(specifyDate: LocalDate) {
        this.dayOfWeek = specifyDate.dayOfWeek
        this.specifiedDate = specifyDate
    }

    fun satisfiedBy(selectedDate: LocalDate): Boolean =
        this.specifiedDate?.isEqual(selectedDate) ?: (this.dayOfWeek == selectedDate.dayOfWeek)

    fun addSlot(
        startTimeInclusive: LocalTime,
        endTimeInclusive: LocalTime,
    ) {
        this.addSlot(slot = TimeSlot(startTimeInclusive, endTimeInclusive))
    }

    fun addSlot(slot: TimeSlot) {
        check(!this.isOverlappingWithExistingTimeSlots(other = slot)) {
            "The specified time slot overlaps with an existing time slot."
        }

        this._slots += slot
        this._slots.sortBy { it.startTimeInclusive }
    }

    fun overlapsWith(other: Timeline): Boolean {
        if (!isSameDay(other)) return false

        return other._slots.any(this::isOverlappingWithExistingTimeSlots)
    }

    private fun isSameDay(other: Timeline): Boolean =
        this.specifiedDate?.isEqual(other.specifiedDate) ?: (this.dayOfWeek == other.dayOfWeek)

    private fun isOverlappingWithExistingTimeSlots(other: TimeSlot): Boolean {
        // 새로운 슬롯이 들어갈 위치를 찾음
        val position: Int = determineTimeSlotPosition(timeline = this, timeSlot = other)

        // 이전 슬롯과 겹치는지 확인
        val overlapWithPrevious: Boolean =
            position > 0 && this._slots[position - 1].endTimeInclusive > other.startTimeInclusive

        // 다음 슬롯과 겹치는지 확인
        val overlapWithNext: Boolean =
            position < this._slots.size && this._slots[position].startTimeInclusive < other.endTimeInclusive

        return overlapWithPrevious || overlapWithNext
    }

    private fun determineTimeSlotPosition(
        timeline: Timeline,
        timeSlot: TimeSlot,
    ): Int =
        timeline._slots
            .binarySearch { it.startTimeInclusive.compareTo(timeSlot.startTimeInclusive) }
            .let { if (it < 0) -(it + 1) else it }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this.javaClass != other?.javaClass) return false

        other as Timeline

        if (this.dayOfWeek != other.dayOfWeek) return false
        if (this.specifiedDate != other.specifiedDate) return false
        if (this._slots != other._slots) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.dayOfWeek.hashCode()
        result = 31 * result + (this.specifiedDate?.hashCode() ?: 0)
        result = 31 * result + this._slots.hashCode()
        return result
    }

    override fun toString(): String = "Timeline(dayOfWeek=${this.dayOfWeek}, specifiedDate=${this.specifiedDate}, slots=${this._slots})"

    companion object {
        fun fullTime(specifyDate: LocalDate): Timeline =
            Timeline(specifyDate = specifyDate).apply { this.addSlot(LocalTime.MIN, LocalTime.MAX) }

        fun fullTime(dayOfWeek: DayOfWeek): Timeline = Timeline(dayOfWeek = dayOfWeek).apply { this.addSlot(LocalTime.MIN, LocalTime.MAX) }
    }
}
