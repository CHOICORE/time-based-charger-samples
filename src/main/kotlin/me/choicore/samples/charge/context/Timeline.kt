package me.choicore.samples.charge.context

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

    fun satisfiedBy(date: LocalDate): Boolean = this.specifiedDate?.isEqual(date) ?: (this.dayOfWeek == date.dayOfWeek)

    fun addSlot(
        startTimeInclusive: LocalTime,
        endTimeInclusive: LocalTime,
    ) {
        this.addSlot(TimeSlot(startTimeInclusive, endTimeInclusive))
    }

    fun addSlot(slot: TimeSlot) {
        check(!this.isOverlappingWithExistingTimeSlots(slot)) {
            "The specified time slot overlaps with an existing time slot."
        }

        this._slots.add(slot)
        this._slots.sortBy { it.startTimeInclusive }
    }

    fun overlapsWith(other: Timeline): Boolean {
        if (!isSameDay(other)) return false

        return this._slots.any { slot ->
            val position: Int = getExistingTimeSlotPosition(other, slot)

            val overlapsWithPrevious = position > 0 && other._slots[position - 1].overlapsWith(slot)
            val overlapsWithCurrent = position < other._slots.size && other._slots[position].overlapsWith(slot)
            val overlapsWithNext = position + 1 < other._slots.size && other._slots[position + 1].overlapsWith(slot)

            overlapsWithPrevious || overlapsWithCurrent || overlapsWithNext
        }
    }

    private fun isSameDay(other: Timeline): Boolean = specifiedDate?.isEqual(other.specifiedDate) ?: (dayOfWeek == other.dayOfWeek)

    /**
     * 새로운 TimeSlot이 기존의 슬롯들과 겹치는지 확인합니다.
     *
     * @param other 추가할 새로운 시간 슬롯.
     * @return 새로운 슬롯이 기존 슬롯들과 겹치는 경우 true, 그렇지 않으면 false.
     */
    private fun isOverlappingWithExistingTimeSlots(other: TimeSlot): Boolean {
        val position: Int = getExistingTimeSlotPosition(this, other)
        // 이전 슬롯과 겹치는지 확인
        val overlapWithPrevious: Boolean =
            position > 0 && this._slots[position - 1].endTimeInclusive > other.startTimeInclusive

        // 다음 슬롯과 겹치는지 확인
        val overlapWithNext: Boolean =
            position < this._slots.size && _slots[position].startTimeInclusive < other.endTimeInclusive

        return overlapWithPrevious || overlapWithNext
    }

    private fun getExistingTimeSlotPosition(
        other: Timeline,
        slot: TimeSlot,
    ): Int =
        other._slots
            .binarySearch { it.startTimeInclusive.compareTo(slot.startTimeInclusive) }
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
        fun fullTime(date: LocalDate): Timeline = Timeline(date).apply { this.addSlot(LocalTime.MIN, LocalTime.MAX) }

        fun fullTime(dayOfWeek: DayOfWeek): Timeline = Timeline(dayOfWeek).apply { this.addSlot(LocalTime.MIN, LocalTime.MAX) }
    }
}
