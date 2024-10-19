package me.choicore.samples.charge.context

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TimelineTests {
    @Test
    fun `should return true if the timeline is satisfied by the specified date`() {
        val timeline = Timeline(LocalDate.of(2024, 1, 1))
        val date: LocalDate = LocalDate.of(2024, 1, 1)
        assertThat(timeline.satisfiedBy(date)).isTrue
    }

    @Test
    fun `should return false if the timeline is not satisfied by the specified date`() {
        val timeline = Timeline(LocalDate.of(2024, 1, 1))
        val date: LocalDate = LocalDate.of(2024, 1, 2)
        assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should return false if the timeline is not satisfied by the day of the week`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        val date: LocalDate = LocalDate.of(2024, 10, 17)
        assertThat(date.dayOfWeek).isNotEqualTo(timeline.dayOfWeek)
        assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should return true if the timeline is satisfied by the day of the week`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        val date: LocalDate = LocalDate.of(2024, 10, 18)
        assertThat(timeline.satisfiedBy(date)).isTrue
    }

    @Test
    fun `should return false if the timeline is satisfied by the day of the week, but the specified date is not`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        val date: LocalDate = LocalDate.of(2024, 10, 11)
        assertThat(timeline.dayOfWeek).isEqualTo(date.dayOfWeek)
        assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should add a new time slot without throwing an exception`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        assertThatNoException().isThrownBy {
            timeline.addSlot(LocalTime.of(17, 0), LocalTime.of(19, 0))
        }
    }

    @Test
    fun `should throw an exception when adding overlapping time slot`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        timeline.addSlot(TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0)))

        assertThatThrownBy {
            timeline.addSlot(LocalTime.of(16, 59), LocalTime.of(19, 0))
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should return false if timeline do not have overlapping time slots with another timeline`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        val other = Timeline(LocalDate.of(2024, 10, 18))
        other.addSlot(LocalTime.of(17, 0), LocalTime.of(19, 0))

        assertThat(timeline.overlapsTimeSlotWith(other)).isFalse
        assertThat(other.overlapsTimeSlotWith(timeline)).isFalse
    }

    @Test
    fun `should return true if timeline have overlapping time slots with another timeline`() {
        val specifiedDate: LocalDate = LocalDate.of(2024, 10, 18)

        val timeline = Timeline(specifiedDate)
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        val other = Timeline(specifiedDate)
        other.addSlot(LocalTime.of(16, 0), LocalTime.of(19, 0))

        assertThat(timeline.overlapsTimeSlotWith(other)).isTrue
        assertThat(other.overlapsTimeSlotWith(timeline)).isTrue
    }

    @Test
    fun `should return true if timeline have overlapping time slots with another timeline with multiple slots`() {
        val specifiedDate: LocalDate = LocalDate.of(2024, 10, 18)

        val timeline = Timeline(specifiedDate)
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))
        timeline.addSlot(LocalTime.of(18, 0), LocalTime.of(19, 0))

        val other = Timeline(specifiedDate)
        other.addSlot(LocalTime.of(16, 0), LocalTime.of(19, 0))

        assertThat(timeline.overlapsTimeSlotWith(other)).isTrue
        assertThat(other.overlapsTimeSlotWith(timeline)).isTrue
    }

    @Test
    fun `should new timeline with full time slot for specified date `() {
        val specifiedDate: LocalDate = LocalDate.of(2024, 10, 18)
        val timeline: Timeline = Timeline.fullTime(specifiedDate)
        println(timeline.slots.size)
        assertThat(timeline.slots).hasSize(1)
        assertThat(timeline.slots[0].startTimeInclusive).isEqualTo(LocalTime.MIN)
        assertThat(timeline.slots[0].endTimeInclusive).isEqualTo(LocalTime.MAX)
    }

    @Test
    fun `should new timeline with full time slot for the day of the week`() {
        val dayOfWeek: DayOfWeek = DayOfWeek.FRIDAY
        val timeline: Timeline = Timeline.fullTime(dayOfWeek)
        println(timeline.slots.size)
        assertThat(timeline.slots).hasSize(1)
        assertThat(timeline.slots[0].startTimeInclusive).isEqualTo(LocalTime.MIN)
        assertThat(timeline.slots[0].endTimeInclusive).isEqualTo(LocalTime.MAX)
    }
}
