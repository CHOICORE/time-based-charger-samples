package me.choicore.samples.charge.context

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TimelineTests {
    @Test
    fun `should return true if the timeline is satisfied by the specified date`() {
        val timeline = Timeline(LocalDate.of(2024, 1, 1))
        val date: LocalDate = LocalDate.of(2024, 1, 1)
        Assertions.assertThat(timeline.satisfiedBy(date)).isTrue
    }

    @Test
    fun `should return false if the timeline is not satisfied by the specified date`() {
        val timeline = Timeline(LocalDate.of(2024, 1, 1))
        val date: LocalDate = LocalDate.of(2024, 1, 2)
        Assertions.assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should return false if the timeline is not satisfied by the day of the week`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        val date: LocalDate = LocalDate.of(2024, 10, 17)
        Assertions.assertThat(date.dayOfWeek).isNotEqualTo(timeline.dayOfWeek)
        Assertions.assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should return true if the timeline is satisfied by the day of the week`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        val date: LocalDate = LocalDate.of(2024, 10, 18)
        Assertions.assertThat(timeline.satisfiedBy(date)).isTrue
    }

    @Test
    fun `should return false if the timeline is satisfied by the day of the week, but the specified date is not`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        val date: LocalDate = LocalDate.of(2024, 10, 11)
        Assertions.assertThat(timeline.dayOfWeek).isEqualTo(date.dayOfWeek)
        Assertions.assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should add a new time slot without throwing an exception`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        Assertions.assertThatNoException().isThrownBy {
            timeline.addSlot(LocalTime.of(17, 0), LocalTime.of(19, 0))
        }
    }

    @Test
    fun `should throw an exception when adding overlapping time slot`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        Assertions
            .assertThatThrownBy {
                timeline.addSlot(LocalTime.of(16, 59), LocalTime.of(19, 0))
            }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should return false if timeline do not have overlapping time slots`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        val otherTimeline = Timeline(LocalDate.of(2024, 10, 18))
        otherTimeline.addSlot(LocalTime.of(17, 0), LocalTime.of(19, 0))

        Assertions.assertThat(timeline.overlapsTimeSlotWith(otherTimeline)).isFalse
    }

    @Test
    fun `should return true if timeline have overlapping time slots`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        val otherTimeline = Timeline(LocalDate.of(2024, 10, 18))
        otherTimeline.addSlot(LocalTime.of(16, 0), LocalTime.of(19, 0))

        Assertions.assertThat(timeline.overlapsTimeSlotWith(otherTimeline)).isTrue
    }

    @Nested
    inner class TimeSlotTests {
        @Test
        fun `should not throw an exception for valid time slot`() {
            Assertions.assertThatNoException().isThrownBy {
                Timeline.TimeSlot(LocalTime.of(9, 0), LocalTime.of(9, 1))
            }
        }

        @Test
        fun `should throw an exception for time slot with same start and end time`() {
            Assertions
                .assertThatIllegalArgumentException()
                .isThrownBy {
                    Timeline.TimeSlot(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
                }
        }

        @Test
        fun `should throw an exception for invalid time slot where start time is after end time`() {
            Assertions
                .assertThatIllegalArgumentException()
                .isThrownBy {
                    Timeline.TimeSlot(LocalTime.MAX, LocalTime.MIN)
                }
        }

        @Test
        fun `should return true if other timeSlot is within the current timeSlot`() {
            val timeSlot = Timeline.TimeSlot(LocalTime.of(9, 0), LocalTime.of(15, 0))
            val otherTimeSlot = Timeline.TimeSlot(LocalTime.of(14, 0), LocalTime.of(19, 0))
            Assertions.assertThat(timeSlot.overlapsWith(otherTimeSlot)).isTrue
        }

        @Test
        fun `should return false if other timeSlot is without the current timeSlot`() {
            val timeSlot = Timeline.TimeSlot(LocalTime.of(9, 0), LocalTime.of(15, 0))
            val otherTimeSlot = Timeline.TimeSlot(LocalTime.of(15, 0), LocalTime.of(19, 0))
            Assertions.assertThat(timeSlot.overlapsWith(otherTimeSlot)).isFalse
        }
    }
}
